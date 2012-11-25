-- LunaTest eXtended Assertions: some more assert functions to lunatest framework

require "lunatest"

--- Test that two tables are equivalent (by value, not by reference)
-- This function recursively test that tables are equal by value, two tables
-- are considerated as equal if their keys and values are equals (and their
-- count is the same).
-- It is not intended to be a general purpose equality function, for example
-- tables as keys are still located by reference.
-- For tables, equality is checked first with == operator (using references or
-- __eq metamethod) and then by value.
-- @param exp
-- @param got
function assert_table_equal(exp, got, msg)
    local key_stack = {}
    local function equal(vexp, vgot)
        if type(vexp) == "table" and type(vgot) == "table" then 
            -- check that values in got tables are as expected
            for k,v in pairs(vexp) do
                table.insert(key_stack, tostring(k))
                if v ~= rawget(vgot, k) then -- first try basic equals, triggering __eq if available
                    local success, err = equal(v, rawget(vgot, k))
                    if not success then return false, err end
                end
                table.remove(key_stack)
            end
            -- checks that got table does not contain any extra value
            for k,_ in pairs(vgot) do
                table.insert(key_stack, tostring(k))
                if rawget(vexp, k) == nil then return false, "unexpected key ["..table.concat(key_stack, "][").."]" end
                table.remove(key_stack)
            end
            return true
        elseif vexp == vgot then return true
        else return false, string.format("expected '%s' (%s) got '%s' (%s) for key [%s]", tostring(vexp), type(vexp), tostring(vgot), type(vgot), table.concat(key_stack, "]["))
        end
    end
    
    local success, err = equal(exp, got)
    if not success then fail(msg and (msg.."("..err..")" ) or err) end
end

--- Test that a table contains at least a set of expected values
-- This function test recursively that the values of all keys in exp are equal in got
function assert_table_subset(exp, got, msg)
    local key_stack = {}
    local function equal(vexp, vgot)
        if type(vexp) == "table" and type(vgot) == "table" then 
            for k,v in pairs(vexp) do
                table.insert(key_stack, tostring(k))
                if v ~= rawget(vgot, k) then -- first try basic equals, triggering __eq if available
                    local success, err = equal(v, rawget(vgot, k))
                    if not success then return false, err end
                end
                table.remove(key_stack)
            end
            return true
        elseif vexp == vgot then return true
        else return false, string.format("expected '%s' (%s) got '%s' (%s) for key [%s]", tostring(vexp), type(vexp), tostring(vgot), type(vgot), table.concat(key_stack, "]["))
        end
    end
    
    local success, err = equal(exp, got)
    if not success then fail(msg and (msg.."("..err..")" ) or err) end
end

--- Creates a data-oriented test which calls test function with each provided data set.
-- @param data table of tables (sequences) that contains test case data
-- @param test test function, called with each row of data as argument
function data_oriented_factory(data, test)
    return function()
        for i, case in ipairs(data) do
            local success, err = pcall(test, unpack(case))
            if not success then
                if type(err) == "table" and err.msg then
                    err.msg = "[Running data set #"..tostring(i).."] " .. err.msg
                end
                error(err)
            end
        end
    end
end
