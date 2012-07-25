local myfunction()
	return true;
end

for forvar in [0,1,2]
	if myfunction() or orvar then
		print (forvar)
	end
end
