do local _ = {
		unknownglobalvars = {
			{
				name = "require",
				shortdescription = "",
				description = "",
				occurrences = {
					{
						sourcerange = {
							min = 11,
							max = 17
						}
						--[[table: 0x8469b58]],
						definition = nil --[[ref]],
						tag = "MIdentifier"
					}
					--[[table: 0x854bbd8]]
				}
				--[[table: 0x84fd678]],
				sourcerange = {
					min = 11,
					max = 17
				}
				--[[table: 0x84fd6a0]],
				tag = "item"
			}
			--[[table: 0x84fd650]]
		}
		--[[table: 0x83f3040]],
		content = {
			localvars = {
				{
					item = {
						type = {
							modulename = "module",
							returnposition = 1,
							tag = "moduletyperef"
						}
						--[[table: 0x853b7f0]],
						shortdescription = "",
						name = "l",
						occurrences = {
							{
								sourcerange = {
									min = 7,
									max = 7
								}
								--[[table: 0x83c07a8]],
								definition = nil --[[ref]],
								tag = "MIdentifier"
							}
							--[[table: 0x83c0780]]
						}
						--[[table: 0x84ba808]],
						sourcerange = {
							min = 7,
							max = 7
						}
						--[[table: 0x853b7c8]],
						description = "",
						tag = "item"
					}
					--[[table: 0x84d5a80]],
					scope = {
						min = 0,
						max = 0
					}
					--[[table: 0x85492d0]]
				}
				--[[table: 0x853b818]]
			}
			--[[table: 0x85609e8]],
			sourcerange = {
				min = 1,
				max = 10000
			}
			--[[table: 0x8560a10]],
			content = {
				{
					sourcerange = {
						min = 11,
						max = 26
					}
					--[[table: 0x8401ef0]],
					func = nil --[[ref]],
					tag = "MCall"
				}
				--[[table: 0x8469b80]],
				nil --[[ref]]
			}
			--[[table: 0x85609c0]],
			tag = "MBlock"
		}
		--[[table: 0x83ecf10]],
		tag = "MInternalContent"
	}
	--[[table: 0x8577a40]];
	_.unknownglobalvars[1].occurrences[1].definition = _.unknownglobalvars[1];
	_.content.localvars[1].item.occurrences[1].definition = _.content.localvars[1].item;
	_.content.content[1].func = _.unknownglobalvars[1].occurrences[1];
	_.content.content[2] = _.content.localvars[1].item.occurrences[1];
	return _;
end