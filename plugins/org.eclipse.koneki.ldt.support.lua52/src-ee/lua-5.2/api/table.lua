-------------------------------------------------------------------------------
-- Table Manipulation.
-- This library provides generic functions for table manipulation. It provides
-- all its functions inside the table `table`.
--
-- Remember that, whenever an operation needs the length of a table, the table
-- should be a proper sequence or have a `__len` metamethod. All
-- functions ignore non-numeric keys in tables given as arguments.
--
-- For performance reasons, all table accesses (get/set) performed by these functions are raw. 
-- @module table



-------------------------------------------------------------------------------
-- Given a list where all elements are strings or numbers, returns the string
-- `list[i]..sep..list[i+1] ... sep..list[j]`. The default value for `sep` is 
-- the empty string, the default for `i` is `1`, and the default for `j` is `#list`.
-- If `i` is greater than `j`, returns the empty string. 
-- @function [parent=#table] concat
-- @param #table list list to handle.
-- @param #string sep the separator (optional, empty string by default). 
-- @param #number i start index (optional, 1 by default).
-- @param #number j end index (optional, `list` length by default)
-- @return #string the concatenated list.
-- @return #string empty string if `i` is greater than `j`

-------------------------------------------------------------------------------
-- Inserts element `value` at position `pos` in `list`, shifting up
-- other elements to open space, if necessary. The default value for `pos` is
-- `n+1`, where `n` is the length of the list, so that a call
-- `table.insert(t,x)` inserts `x` at the end of list `t`.
-- @function [parent=#table] insert
-- @param #table list list to modify.
-- @param #number pos index of insertion (optional, insert at the end of the table by default)
-- @param value value to insert.

-------------------------------------------------------------------------------
-- Returns a new table with all parameters stored into keys 1, 2, etc. and with 
-- a field `n` with the total number of parameters. Note that the resulting
-- table may not be a sequence. 
-- @function [parent=#table] pack
-- @param ... items to pack
-- @return #table the created table from given parameters

-------------------------------------------------------------------------------
-- Removes from list the element at position `pos`, shifting down the elements
-- `list[pos+1], list[pos+2], ..., list[#list]` and erasing element `list[#list]`.
-- Returns the value of the removed element. The default value for pos is `#list`,
-- so that a call `table.remove(t)` removes the last element of list `t`. 
-- @function [parent=#table] remove
-- @param #table list list to modify.
-- @param #number pos index of deletion (optional, length of the list by default)

-------------------------------------------------------------------------------
-- Sorts list elements in a given order, in-place, from `list[1]` to `list[#list]`.
-- If `comp` is given, then it must be a function that receives two list elements
-- and returns true when the first element must come before the second in the
-- final order (so that `not comp(list[i+1],list[i]`) will be true after the sort).
-- If `comp` is not given, then the standard Lua operator < is used instead. 
-- 
-- The sort algorithm is not stable; that is, elements considered equal by the
-- given order may have their relative positions changed by the sort.
-- @function [parent=#table] sort
-- @param #table list list to sort.
-- @param comp a function which take two lists and returns true when the first is less than the second (optional).

-------------------------------------------------------------------------------
--  Returns the elements from the given table. This function is equivalent to
--
--     return list[i], list[i+1], ..., list[j]
--
-- By default, `i` is 1 and `j` is `#list`. 
-- @function [parent=#table] unpack
-- @param #table list list to unpack
-- @param #number i start index (optional, 1 by default).
-- @param #number j end index (optional, length of the list by default).
-- @return Return each table elements as separated values

-------------------------------------------------------------------------------

return nil
