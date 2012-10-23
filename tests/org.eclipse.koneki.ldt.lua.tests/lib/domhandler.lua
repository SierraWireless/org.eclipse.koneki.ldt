---
-- A custom Dom handler for HTML files
--
--  
--  Features:
--  =========
--      domHandler          - Generate DOM-like node tree
--  
--  API:
--  ====
--      Must be called as handler function from xmlParser
--      and implement XML event callbacks (see xmlParser.lua 
--      for callback API definition)
--
--      domHandler:
--      -----------
--
--      domHandler generates a DOM-like node tree  structure with 
--      a single ROOT node parent - each node is a table comprising 
--      fields below.
--  
--      node = { _name = <Element Name>,
--              _type = ROOT|ELEMENT|TEXT|COMMENT|PI|DECL|DTD,
--              _attr = { Node attributes - see callback API },
--              _parent = <Parent Node>
--              _children = { List of child nodes - ROOT/NODE only }
--            }
--
--      The dom structure is capable of representing any valid XML document
--
--      
--
--  Options
--  =======
--      domHandler.options.(comment|pi|dtd|decl)Node = bool 
--          
--          - Include/exclude given node types
--  
--  Usage
--  =====
--      Pased as delegate in xmlParser constructor and called 
--      as callback by xmlParser:parse(xml) method.
--
--  License:
--  ========
--
--      This code is freely distributable under the terms of the Lua license
--      (<a href="http://www.lua.org/copyright.html">http://www.lua.org/copyright.html</a>)
--
--  History
--  =======
--  Renamed domhandler.lua from handler.lua and cleaning all the others handlefunction to leave only the DOM one.
--
--  $Id: handler.lua,v 1.1.1.1 2001/11/28 06:11:33 paulc Exp $
--
--  $Log: handler.lua,v $
--  Revision 1.1.1.1  2001/11/28 06:11:33  paulc
--  Initial Import
--@author Paul Chakravarti (paulc@passtheaardvark.com)<p/>
--@module domhandler

local M = {}

local function heavytrim(string)
	repeat
		string = string:gsub("^%s*(.-)%s*$","%1")
		string = string:gsub("^\\n*(.-)\\n*$","%1")
		
		local find = string:find("^(%s+).-(%s+)$")
		find = find or string:find("^(\\n+).-(\\n+)$")
	until find == nil
	
	return string
end

--- domHandler
function M.createhandler() 
    local obj = {}
    obj.options = {commentNode=1,piNode=1,dtdNode=1,declNode=1}
    obj.root = { _children = {n=0}, _type = "ROOT" }
    obj.current = obj.root
    obj.starttag = function(self,t,a)
            local node = { _type = 'ELEMENT', 
                           _name = t, 
                           _attr = a, 
                           _parent = self.current, 
                           _children = {n=0} }
            table.insert(self.current._children,node)
            self.current = node
    end
    obj.endtag = function(self,t,s)
            if t ~= self.current._name then
                --error("XML Error - Unmatched Tag ["..s..":"..t.."]\n")
            end
            self.current = self.current._parent
    end
    obj.text = function(self,t)
    		if (self.current._name ~= "pre") then
    			t = heavytrim(t)
    		end
    		if (t ~= "") then
	            local node = { _type = "TEXT", 
	                           _parent = self.current, 
	                           _text = t }
	            table.insert(self.current._children,node)
            end
    end
    obj.comment = function(self,t)
            if self.options.commentNode then
                local node = { _type = "COMMENT", 
                               _parent = self.current, 
                               _text = t }
                table.insert(self.current._children,node)
            end
    end
    obj.pi = function(self,t,a)
            if self.options.piNode then
                local node = { _type = "PI", 
                               _name = t,
                               _attr = a, 
                               _parent = self.current } 
                table.insert(self.current._children,node)
            end
    end
    obj.decl = function(self,t,a)
            if self.options.declNode then
                local node = { _type = "DECL", 
                               _name = t,
                               _attr = a, 
                               _parent = self.current }
                table.insert(self.current._children,node)
            end
    end
    obj.dtd = function(self,t,a)
            if self.options.dtdNode then
                local node = { _type = "DTD", 
                               _name = t,
                               _attr = a, 
                               _parent = self.current }
                table.insert(self.current._children,node)
            end
    end
    obj.cdata = obj.text
    return obj
end

return M

