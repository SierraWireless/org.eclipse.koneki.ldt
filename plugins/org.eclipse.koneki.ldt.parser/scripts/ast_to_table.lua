--#!/usr/bin/lua
--------------------------------------------------------------------------------
--  Copyright (c) 2009-2011 Sierra Wireless.
--  All rights reserved. This program and the accompanying materials
--  are made available under the terms of the Eclipse Public License v1.0
--  which accompanies this distribution, and is available at
--  http://www.eclipse.org/legal/epl-v10.html
-- 
--  Contributors:
--       Kevin KIN-FOO <kkinfoo@sierrawireless.com>
--           - initial API and implementation and initial documentation
--------------------------------------------------------------------------------

-- Thoses are hashmaps to link nodes to their ID,
-- in order to enable direct access, time efficient
local idToNode = {}
local nodeToId = {}
local publicDeclarationsIDs = {}
local localDeclarationsIDs = {}
local parent = {}

--
-- Provides distinct IDs calls after calls
-- @return int
--
local id = 0
local function getID()
  id = id + 1
  return id
end


--
-- Assign name to `Function and `Table nested in `Pairs like:
--	<code>
--	local table = {
--		nestedTable={},
--		nestedFunction=function()end
--	}
--	</code>
-- @param id Number identifier of `Table wich potentially contains
--	`Pairs
local function nameNestedPair(id)
	assert(type(id)=='number', "'number' expected")
	local node = idToNode[ id ]
	for k, value in pairs( node ) do
		if value.tag == "Pair" then
			-- Compose human redable name for 
			local index, val = value[1], value[2]
			if val.tag == "Function" or val.tag == "Table" then
				val.identifier = nodeToId[index]
			end
		end
	end
end
--
-- Assing name to "valuable" nodes, such as:
-- * `Function
-- * `Table
--
-- @param ast AST of "table" type
--
local function matchDeclaration( ast )
	--
	--	Checks if node is already indexed in internal hashmaps
	--
	--	@param node	table Node to localize in hashmaps
	--
	local function registered( node )
		assert(
			type(node) == 'table',
			"Node of 'table' type expected.'"..type(node).."' found"
		)
		return nodeToId[ node ] ~= nil
	end
	--
	-- Cache local declarations in a global table, indexed nodes are `Local
	-- and `Localrec
	--
	local function cacheDeclaration(hash, t)
		assert( type(hash) == 'table' )
		assert( type(t)    == 'table' )
		for declaration, occurrences in pairs(t) do
			if registered(declaration) then
				table.insert(hash, nodeToId[ declaration ])
			end
		end
	end
	local function cacheGlobalDeclaration(hash, t)
		local function isCall( node  )
			if not node then return false end
			if node.tag and node.tag =="Call" or node.tag == "Invoke" then
				return true
			end
			return isCall(idToNode[parent[nodeToId[node]]])
		end
		assert( type(hash) == 'table' )
		assert( type(t)    == 'table' )
		for name, occurences in pairs( t ) do
			for id, node in pairs(occurences) do
				if registered(node) and not isCall(node) then
					table.insert(hash, nodeToId[ node ])
				end
			end
		end
	end
	--
	-- Browse left side node in order to compose an human redeable
	-- name from declaration identifier
	--
	-- @param id Numeric identifier of nodes-to-name container such as:
	--	<li>`Local</li>
	--	<li>`Localrec</li>
	--
	local function fetchIdentifier( id )

		-- Compose human redeable name from identifiers and assign it
		-- to every declaration
		local node = idToNode[id]

		-- Dealing with `Forin and `Fornum
		if node.tag == 'Forin' or node.tag == 'Fornum' then
			-- Nothing to as there no declaration with nameless nodes
			return
		end

		-- Dealing with `Local and `Localrec
		local identifiers, declarations = node[1], node[2]
		for index, declaration in pairs( declarations ) do

			-- Avoid 'lineinfo'
			if ( type(index) == 'number' ) then

				-- Naming `Table and `Function nested in `Pair
				if declaration.tag == "Table" then
					nameNestedPair( nodeToId[declaration] )
				end

				-- Associate identifier id when available
				if identifiers[index] then
					declaration["identifier"] = nodeToId[identifiers[index]]
				else
					declaration["identifier"] = 0
				end
			end
		end
	end

	--
	-- Retrieve identifier of enclosing parent node. Useful to deal with
	-- global declarations
	--
	-- @param id Number identifier of declaration node
	-- @return Identifier of parent node when available
	--
	local function topExpression(identifier)
		local node = idToNode[identifier]
		if not node or node.tag and ( node.tag == "Set"
			or node.tag == "Local"
			or node.tag == "Localrec"
			or node.tag == "Pair"
			or node.tag == "While"
			or node.tag == "If"
			or node.tag == "Fornum"
			or node.tag == "Return"
			or node.tag == "Forin" ) then
				return identifier
		end
		return topExpression(parent[identifier])
	end

	--
	-- Provides node position in parent node children list
	--
	-- @param	index Number identifier of position requested node
	-- @return	Number position of node in parent chunk, nil if position not
	-- 	found
	--
	local function getNodePositionInParent(index)
		assert(type(index) == "number", "'number' expected")
		local parentId= topExpression(index)
		-- Locate chunk identifier
		local nodes = idToNode[parentId][1]
		for position, node in pairs(nodes) do
			if node == idToNode[index] then return position end
		end
	end
	--
	-- Browse right side of parent node in order to compose an human
	-- redeable name from declaration identifier
	--
	-- @param id Numeric identifier of node containing name
	--
	local function fetchDeclaration( id )
		-- Get position of node to name in parent chunk, in order to handle
		-- composites statements like: 
		-- local int, f = 0, function()end
		local nodePosition = getNodePositionInParent(id)
		local parentNode = idToNode[topExpression(id)]
		-- Browse left side of parent node, to find declaration identifier
		if parentNode then
			-- Retrieve identifier node right side 
			local rightSide = parentNode[2]
			-- No right side to deal with
			if not rightSide then return end
			local relatedDeclaration= rightSide[nodePosition]
			if relatedDeclaration then
				-- Name `Function and `Table nested in `Pair
				if relatedDeclaration.tag == "Table" then
					nameNestedPair( nodeToId[ relatedDeclaration ] )
				end		
				-- Assign name to declaration
				relatedDeclaration["identifier"]=id
			end
		end
	end
	-- Sort variables in source 
	assert(
		type(ast) == 'table',
		"AST of 'table' type expected, "..type(ast).." found."
	)
	-- Seek for local and global declarations
	require 'metalua.walk.bindings'
	local declareds, leftovers = bindings( ast )
	cacheDeclaration(localDeclarationsIDs, declareds)
	cacheGlobalDeclaration(publicDeclarationsIDs, leftovers)

	-- Now that AST is indexed and local declarations separated from globals
	-- ones, let's back patch declaration with their identifier name
	for key, declaration in pairs( localDeclarationsIDs ) do
		-- Here we are dealing with declation nodes such as
		--	`Function and `Table
		-- The aim is to fetch their name from according left side expression
		fetchIdentifier( declaration )
	end

	for key, identifier in pairs( publicDeclarationsIDs ) do
		--
		-- Locate top node identifier of composite identifier such as `Index
		-- and `Invoke
		--
		-- @param	id Number identifier of node supposed part of composite
		--			identifier
		-- @return	Number identifier of composite identifier top node
		--
		local function topIndex(id)
			assert(type(id)=="number")
			local node, parentNode= idToNode[id], idToNode[ parent[id] ]
			if not parentNode.tag or
				( parentNode.tag~="Index" and parentNode.tag~="Invoke" )
			then return id end
			return topIndex( parent[id] )
		end

		-- Here we are dealing with declarations identifiers such as:
		--	`Index and `Id
		-- The aim is to compose their name then patch right side expressions
		fetchDeclaration( topIndex( identifier ) )
	end
end

--
-- Assign an ID to every node in the AST
-- 
-- Assign an id to every table and sub-table of the given one,
-- except the ones named "lineinfo"
--
-- @param ADT to index
--
function index( ast )
 	local function doIndex( adt )
		local function childNodes( ast )
			local nodes = {}
	    	for k, v in ipairs( ast ) do
	      		if type(v) == "table" then
					nodes[ #nodes + 1 ] = v
	      		end
	    	end
	    	return nodes
	  	end
 		-- Index node
		local id = getID()
		idToNode[ id ] = adt
	 	nodeToId[ adt ] = id

		-- Index child nodes
		for k,v in ipairs( childNodes(adt) )do
	    	if type(v) == "table" then
	      		doIndex( v )
	    	end
		end
	end
  	local function rememberParents( id )
  		assert( type(id) == 'number' )
  		for k, child in pairs( children(id) ) do
  			table.insert(parent, child, id)
  			rememberParents(child)
  		end
  	end
	--
	-- Flush previous indexes to ensure consistance
	--
 	idToNode = {}
 	nodeToId = {}

 	--
 	-- Reference nodes in hash table in order to have direct access
	--
 	doIndex( ast )

	--
 	-- Append declaration data on declaration nodes
	--
	publicDeclarationsIDs = {}
	localDeclarationsIDs = {}
	if #idToNode > 0 then
		rememberParents( 1 )
	end
	matchDeclaration( ast )
end

--
-- Retrurn children of a node
--
-- @param  int	 ID of node to parse
-- @return table Child nodes
-- @return int	 Count of child nodes
--
function children( id )
  local child = {}
if not idToNode[ id ] then print("problem with "..id) end

  for  k,v in ipairs( idToNode[ id ] ) do
    if type(v) == "table" and k ~= "lineinfo" then
      child[ #child + 1 ] = nodeToId[ v ]
    end
  end
  return child, #child
end

-- 
-- Get node's identifier index number
--
-- @param int ID of requested node
-- @return number identifier id or 0 while identifier is not available
-- 
function getIdentifierId( id )
  return idToNode[ id ][ 'identifier' ] or ""
end

-- 
-- Get node's identifier name
--
-- @param int ID of requested node
-- @return identifier name, empty string while identifier is not available
--
function getIdentifierName(id)
	-- Compose name
	local node = idToNode[ id ] 
	require 'metalua.ast_to_string'
	local human = ast_to_string(node)
	if string.sub(human, #human -2) == " ()" then
		human =	string.sub(human,1, #human -3)
	elseif string.find(human, '"') then
		human = string.sub(human, 2, #human-1)
	end
	return  human
end

-- 
-- Get node's tag
--
-- @param  Number ID of node of requested tag
-- @return String
--
function getTag( id )
  return idToNode[id]["tag"]
end

function getNode( id )
  return idToNode[ id ]
end

-- 
-- Get node's value
--
-- @param int ID of requested node
-- 
function getValue( id )
  return idToNode[ id ][ 1 ]
end

-- 
-- Get node's start position in source
--
-- @param int ID of requested node
-- 
function getStart( id )
  local node = idToNode[ id ]
  if node and node[ 'lineinfo' ] then
	return tonumber(node[ 'lineinfo' ]['first'][3])
  end
  return 0
end

-- 
-- Get node's end position in source
--
-- @param int ID of requested node
-- 
function getEnd( id )
  assert (type(id == "number"), "No line info for node "..id)
  local node = idToNode[ id ]
  if node and node [ 'lineinfo' ] then
	return tonumber(node[ 'lineinfo' ]['last'][3])
  end
  return 0
end

--
-- Indicates if line informations are available for a node
-- @param id	ID of requested node
-- 
function hasLineInfo( id )
  return type(idToNode[ id ].lineinfo) == "table"
end

function hasDeclarations( id )
	assert(type(id) == 'number', "Number expected '"..type(id).."' found.")
	local doesNodeExists = type(idToNode[ id ]) == 'table'
	return doesNodeExists and type(idToNode[ id ].occurrences) == 'table'
end

function hasUsage( id )
	assert(type(id) == 'number', "Number expected '"..type(id).."' found.")
	local doesNodeExists = type(idToNode[ id ]) == 'table'
	return hasDeclarations( id )and #(idToNode[ id ].occurrences) > 0
end

function getOccurrencies( id )
	--
	--	Find declarations assotiated with this ID
	--
	assert(type(id) == 'number', "Number expected '"..type(id).."' found.")
	local ids = {}
	
	-- Fetch available declarations
	if hasDeclarations(id) then
		-- Search for nodes assotiated with declaration is hash map
		local occur = idToNode[ id ].occurrences
		assert( occur ~= nil )
		for k, v in pairs( occur ) do
			-- Register every occurence defnition in table
			if type(v) == 'table' then
				for k, occ in ipairs( v ) do
					table.insert(ids, occ)
				end
			end
		end
	end
	-- Return empty table where no declarations are available
	return ids
end

function getDeclarationsIDs ()
	return localDeclarationsIDs,#localDeclarationsIDs
end

function getGlobalDeclarationsIDs ()
	return publicDeclarationsIDs,#publicDeclarationsIDs
end
function getParent(id)
	if type(parent[id]) == 'number' then
		return parent[ id ]
	end
	return nil
end
-- Parse files given from command line
if arg and #arg > 0 then
	local start = os.time()
	local path="/d/LuaEclipse/plugins/org.eclipse.koneki.ldt.metalua.32bits/"
	package.path = path.."?.luac;"..path.."?.lua;"..package.path 
	local verbose = arg[1] and arg[1]=="-v"
	local firstArgPosition = verbose and 2 or 1
	local good, bad, errors = {},{},{}
	require 'metalua.compiler'
	for position,filename in pairs(arg) do
		if firstArgPosition <= position then
			print(math.floor(position*100/#arg).."%\t"..filename)
			local file = io.open(filename, 'r')
			local source = file:read("*all")
			file:close()
			local tree = mlc.luastring_to_ast( source )
			if verbose then
				print( source )
				table.print(tree, "nohash", 1)
			end
			-- Initialising node identification
			id=0
			local status, err = pcall( index, tree )
			if status then
				table.insert( good , filename )
			else
				table.insert( bad , filename )
				table.insert( errors, err )
			end
		end
	end
	print("Success\n\t"..table.concat(good, "\n\t") )
	if #bad > 0 then
		print("Failures")
		for k,v in pairs(bad) do
			print("\t"..v)
			print("\t\t"..errors[k])
		end
		local percent = #good > 0 and ( #good/(#good+#bad) ) * 100 or 0
		print(math.floor(percent) .."% of good files")
	end
	print ("in "..os.difftime(os.time(), start).."s")
end
