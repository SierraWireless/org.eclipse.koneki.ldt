do local _ = {
		unknownglobalvars = {
			{
				name = "d",
				shortdescription = "",
				description = "",
				occurrences = {
					{
						sourcerange = {
							min = 1,
							max = 1
						}
						--[[table: 0x84a6640]],
						definition = nil --[[ref]],
						tag = "MIdentifier"
					}
					--[[table: 0x84a65d8]],
					{
						sourcerange = {
							min = 9,
							max = 9
						}
						--[[table: 0x8513290]],
						definition = nil --[[ref]],
						tag = "MIdentifier"
					}
					--[[table: 0x8583b98]]
				}
				--[[table: 0x8593580]],
				sourcerange = {
					min = 1,
					max = 1
				}
				--[[table: 0x85935a8]],
				tag = "item"
			}
			--[[table: 0x8593470]]
		}
		--[[table: 0x84accb0]],
		content = {
			localvars = {

			}
			--[[table: 0x84acda0]],
			sourcerange = {
				min = 1,
				max = 10000
			}
			--[[table: 0x84acdc8]],
			content = {
				nil --[[ref]],
				nil --[[ref]]
			}
			--[[table: 0x84acd78]],
			tag = "MBlock"
		}
		--[[table: 0x84accd8]],
		tag = "MInternalContent"
	}
	--[[table: 0x84acc10]];
	_.unknownglobalvars[1].occurrences[1].definition = _.unknownglobalvars[1];
	_.unknownglobalvars[1].occurrences[2].definition = _.unknownglobalvars[1];
	_.content.content[1] = _.unknownglobalvars[1].occurrences[1];
	_.content.content[2] = _.unknownglobalvars[1].occurrences[2];
	return _;
end