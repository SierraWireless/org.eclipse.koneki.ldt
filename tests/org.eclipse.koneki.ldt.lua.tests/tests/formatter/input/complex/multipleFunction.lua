----
-- multiple function to process
--
function foo() 
function innerfoo()
function innerinnerfoo()
print "foo"
end
innerinnerfoo()
end
innerfoo()
end
foo()