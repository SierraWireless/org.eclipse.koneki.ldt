/* Bare minimal Debug API functions to implement a POSIX debugger transport backend.
 * Contains additional server functions to run tests with it too, to enable them compile with -DBUILD_SERVER flag. */
#include <lua.h>
#include <lauxlib.h>

#include <math.h>
#include <string.h>
#include <errno.h>

#include <time.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <fcntl.h>

#define MT_NAME ("debugger.transport.core")

/* we dont need upvalues anyway */
#if LUA_VERSION_NUM < 502
#  define luaL_setfuncs(L, t, n) (luaL_register(L, NULL, t))
#endif

/*
 * Socket API
 */

static int pusherrno(lua_State *L, const char *fmt) {
  lua_pushnil(L);
  lua_pushfstring(L, fmt, strerror(errno));
  return 2;
}

static int api_create(lua_State *L) {
  int *sockfd = lua_newuserdata (L, sizeof(int));
  *sockfd = socket(AF_INET, SOCK_STREAM, 0);
  if (sockfd < 0) return pusherrno(L, "Cannot open socket: %s");
  luaL_getmetatable(L, MT_NAME);
  lua_setmetatable(L, -2);
  return 1;
}

static int api_sleep(lua_State *L) {
  struct timespec t;
  double secs = luaL_checknumber(L, 1);
  
  t.tv_sec = floor(secs);
  t.tv_nsec = fmod(secs, 1) * 1000000000;
  nanosleep(&t, NULL);
  return 0;
}

static int api_connect(lua_State *L) {
  int *sockfd = (int *)luaL_checkudata(L, 1, MT_NAME);
  const char *host = luaL_checkstring(L, 2);
  int port = luaL_checkinteger(L, 3);
  struct hostent *server = gethostbyname(host);
  struct sockaddr_in serv_addr;
  
  bzero((char *) &serv_addr, sizeof(serv_addr));
  serv_addr.sin_family = AF_INET;
  bcopy((char *)server->h_addr, (char *)&serv_addr.sin_addr.s_addr, server->h_length);
  serv_addr.sin_port = htons(port);
  if (connect(*sockfd,(struct sockaddr *) &serv_addr,sizeof(serv_addr)) < 0) return pusherrno(L, "Cannot connect to server: %s");
  
  lua_pushboolean(L, 1);
  return 1;
}

static int api_receive(lua_State *L) {
  int *sockfd = (int *)luaL_checkudata(L, 1, MT_NAME);
  size_t nbytes = luaL_checkinteger(L, 2);
  luaL_Buffer buf;
  
  /* luaL_buffinitsize is 5.2 only so don't use it */
  /* FIXME this is really inefficient in nonblock mode because it allocate LUAL_BUFFERSIZE byte likely for nothing */
  luaL_buffinit(L, &buf);
  while(nbytes > 0) {
    char *chunk = luaL_prepbuffer(&buf);
    int nread = read(*sockfd, chunk, (nbytes > LUAL_BUFFERSIZE) ? LUAL_BUFFERSIZE : nbytes);
    if(nread < 0) return pusherrno(L, "Read error: %s");
    else if(nread == 0) { /* LuaSocket compatible behaviour on closed fd */
      luaL_pushresult(&buf);
      lua_pushnil(L);
      lua_pushstring(L, "closed");
      lua_pushvalue(L, -3);
      return 3;
    }
    luaL_addsize(&buf, nread);
    nbytes -= nread;
  }
  
  luaL_pushresult(&buf);
  return 1;
}

static int api_send(lua_State *L) {
  size_t nbytes;
  int *sockfd = (int *)luaL_checkudata(L, 1, MT_NAME);
  const char *data = luaL_checklstring(L, 2, &nbytes);
  
  while(nbytes > 0) {
    int nwritten = write(*sockfd, data, nbytes);
    if(nwritten < 0) return pusherrno(L, "Write error: %s");
    nbytes -= nwritten;
    data += nwritten;
  }
  if(fsync(*sockfd) < 0) {
    return pusherrno(L, "Write error: %s");
  }
  
  lua_pushboolean(L, 1);
  return 1;
}

static int api_settimeout(lua_State *L) {
  int *sockfd = (int *)luaL_checkudata(L, 1, MT_NAME);
  int flags = fcntl(*sockfd, F_GETFL, 0); /* expects a working fcntl, see http://www.kegel.com/dkftpbench/nonblocking.html */
  
  if(flags < 0) return pusherrno(L, "Settimeout error: %s");
  /* just switch between blocking and non blocking (don'h handle timeouts). */
  if(lua_isnil(L, 2)) flags &= ~O_NONBLOCK;
  else                flags |= O_NONBLOCK;
  if(fcntl(*sockfd, F_SETFL, flags) < 0) return pusherrno(L, "Settimeout error: %s");
  
  lua_pushboolean(L, 1);
  return 1;
}

#ifdef BUILD_SERVER
static int api_bind(lua_State *L) {
  struct sockaddr_in serv_addr;
  int *sockfd = (int *)luaL_checkudata(L, 1, MT_NAME);
  const char *addr = luaL_checkstring(L, 2); /* ignored for now, bind to INADDR_ANY */
  int on = 1;
  
  if(setsockopt(*sockfd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0)
    return pusherrno(L, "setsockopt error: %s");
  
  int port = luaL_checkinteger(L, 3);
  serv_addr.sin_family = AF_INET;
  serv_addr.sin_addr.s_addr = INADDR_ANY;
  serv_addr.sin_port = htons(port);
  if(bind(*sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) 
    return pusherrno(L, "Bind error: %s");
  
  lua_pushboolean(L, 1);
  return 1;
}

static int api_listen(lua_State *L) {
  int *sockfd = (int *)luaL_checkudata(L, 1, MT_NAME);
  int backlog = luaL_checkinteger(L, 2);
  if(listen(*sockfd,backlog) < 0) return pusherrno(L, "Listen error: %s");
  lua_pushboolean(L, 1);
  return 1;
}

static int api_accept(lua_State *L) {
  struct sockaddr_in cli_addr;
  socklen_t clilen = sizeof(cli_addr);
  int *srvfd = (int *)luaL_checkudata(L, 1, MT_NAME);
  int *clifd = (int *)lua_newuserdata (L, sizeof(int));
  
  *clifd = accept(*srvfd, (struct sockaddr *) &cli_addr, &clilen);
  if(*clifd < 0) return pusherrno(L, "Accept error: %s");
  
  luaL_getmetatable(L, MT_NAME);
  lua_setmetatable(L, -2);
  return 1;
}
#endif

static int api_close(lua_State *L) {
  int *sockfd = (int *)luaL_checkudata(L, 1, MT_NAME);
  if(*sockfd >= 0) {
    close(*sockfd);
    *sockfd = -1; /* mark fd as unused */
  }
  lua_pushboolean(L, 1);
  return 1;
}

/*
 * Base64 API
 * Public domain, from http://www.tecgraf.puc-rio.br/~lhf/ftp/lua/#lbase64
 */

static const char code[]=
"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

static void encode(luaL_Buffer *b, uint c1, uint c2, uint c3, int n)
{
 unsigned long tuple=c3+256UL*(c2+256UL*c1);
 int i;
 char s[4];
 for (i=0; i<4; i++) {
  s[3-i] = code[tuple % 64];
  tuple /= 64;
 }
 for (i=n+1; i<4; i++) s[i]='=';
 luaL_addlstring(b,s,4);
}

static int api_rawb64(lua_State *L)                /** encode(s) */
{
 size_t l;
 const unsigned char *s=(const unsigned char*)luaL_checklstring(L,1,&l);
 luaL_Buffer b;
 int n;
 luaL_buffinit(L,&b);
 for (n=l/3; n--; s+=3) encode(&b,s[0],s[1],s[2],3);
 switch (l%3)
 {
  case 1: encode(&b,s[0],0,0,1);                break;
  case 2: encode(&b,s[0],s[1],0,2);             break;
 }
 luaL_pushresult(&b);
 return 1;
}

static void decode(luaL_Buffer *b, int c1, int c2, int c3, int c4, int n)
{
 unsigned long tuple=c4+64L*(c3+64L*(c2+64L*c1));
 char s[3];
 switch (--n)
 {
  case 3: s[2]=tuple;
  case 2: s[1]=tuple >> 8;
  case 1: s[0]=tuple >> 16;
 }
 luaL_addlstring(b,s,n);
}

static int api_unb64(lua_State *L)                /** decode(s) */
{
 size_t l;
 const char *s=luaL_checklstring(L,1,&l);
 luaL_Buffer b;
 int n=0;
 char t[4];
 luaL_buffinit(L,&b);
 for (;;)
 {
  int c=*s++;
  switch (c)
  {
   const char *p;
   default:
    p=strchr(code,c); if (p==NULL) return 0;
    t[n++]= p-code;
    if (n==4)
    {
     decode(&b,t[0],t[1],t[2],t[3],4);
     n=0;
    }
    break;
   case '=':
    switch (n)
    {
     case 1: decode(&b,t[0],0,0,0,1);           break;
     case 2: decode(&b,t[0],t[1],0,0,2);        break;
     case 3: decode(&b,t[0],t[1],t[2],0,3);     break;
    }
   case 0:
    luaL_pushresult(&b);
    return 1;
   case '\n': case '\r': case '\t': case ' ': case '\f': case '\b':
    break;
  }
 }
 return 0;
}


static const luaL_Reg sock_table[] = {
  { "connect",    api_connect    },
  { "receive",    api_receive    },
  { "send",       api_send       },
  { "settimeout", api_settimeout },
#ifdef BUILD_SERVER
  { "bind",       api_bind       },
  { "listen",     api_listen     },
  { "accept",     api_accept     },
#endif
  { "close",      api_close      },
  { NULL,         NULL           },
};

static const luaL_Reg module_table[] = {
  { "create", api_create },
  { "sleep",  api_sleep  },
  { "rawb64", api_rawb64 },
  { "unb64",  api_unb64  },
  { NULL,     NULL       },
};

int luaopen_debuggertransport(lua_State *L) {
  /* register sock metatable */
  luaL_newmetatable(L, MT_NAME);
  luaL_setfuncs(L, sock_table, 0);
  lua_pushvalue(L, -1);
  lua_setfield(L, -2, "__index");
  lua_pushcfunction(L, api_close);
  lua_setfield(L, -2, "__gc");
  
  /* module table */
  lua_newtable(L);
  luaL_setfuncs(L, module_table, 0);
  
  return 1;
}
