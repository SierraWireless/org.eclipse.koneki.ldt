do local _ = {
		unknownglobalvars = {

		}
		--[[table: 0x83deb88]],
		content = {
			localvars = {
				{
					item = {
						shortdescription = "",
						name = "d",
						occurrences = {
							{
								sourcerange = {
									min = 7,
									max = 7
								}
								--[[table: 0x849e118]],
								definition = nil --[[ref]],
								tag = "MIdentifier"
							}
							--[[table: 0x8583b40]],
							{
								sourcerange = {
									min = 9,
									max = 9
								}
								--[[table: 0x8579f00]],
								definition = nil --[[ref]],
								tag = "MIdentifier"
							}
							--[[table: 0x8579e98]]
						}
						--[[table: 0x8408cb8]],
						sourcerange = {
							min = 7,
							max = 7
						}
						--[[table: 0x8408ce0]],
						description = "",
						tag = "item"
					}
					--[[table: 0x85342b0]],
					scope = {
						min = 0,
						max = 0
					}
					--[[table: 0x84f6420]]
				}
				--[[table: 0x8408de0]]
			}
			--[[table: 0x83dec78]],
			sourcerange = {
				min = 1,
				max = 10000
			}
			--[[table: 0x83deca0]],
			content = {
				nil --[[ref]],
				nil --[[ref]]
			}
			--[[table: 0x83dec50]],
			tag = "MBlock"
		}
		--[[table: 0x83debb0]],
		tag = "MInternalContent"
	}
	--[[table: 0x83deae8]];
	_.content.localvars[1].item.occurrences[1].definition = _.content.localvars[1].item;
	_.content.localvars[1].item.occurrences[2].definition = _.content.localvars[1].item;
	_.content.content[1] = _.content.localvars[1].item.occurrences[1];
	_.content.content[2] = _.content.localvars[1].item.occurrences[2];
	return _;
end