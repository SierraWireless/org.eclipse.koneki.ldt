do local _ = {
		unknownglobalvars = {
			{
				name = "string",
				shortdescription = "",
				description = "",
				occurrences = {
					{
						sourcerange = {
							min = 17,
							max = 22
						}
						--[[table: 0x8471458]],
						definition = nil --[[ref]],
						tag = "MIdentifier"
					}
					--[[table: 0x84b3490]]
				}
				--[[table: 0x83df100]],
				sourcerange = {
					min = 17,
					max = 22
				}
				--[[table: 0x83bbd48]],
				tag = "item"
			}
			--[[table: 0x83df0d8]]
		}
		--[[table: 0x83d39d0]],
		content = {
			localvars = {
				{
					item = {
						type = {
							expression = {
								sourcerange = {
									min = 17,
									max = 29
								}
								--[[table: 0x836a968]],
								right = "format",
								left = nil --[[ref]],
								tag = "MIndex"
							}
							--[[table: 0x8574e10]],
							returnposition = 1,
							tag = "exprtyperef"
						}
						--[[table: 0x83bbcb8]],
						shortdescription = "",
						name = "sformat",
						occurrences = {
							{
								sourcerange = {
									min = 7,
									max = 13
								}
								--[[table: 0x8436d88]],
								definition = nil --[[ref]],
								tag = "MIdentifier"
							}
							--[[table: 0x83a7598]]
						}
						--[[table: 0x83555a0]],
						sourcerange = {
							min = 7,
							max = 13
						}
						--[[table: 0x8355608]],
						description = "",
						tag = "item"
					}
					--[[table: 0x854be60]],
					scope = {
						min = 0,
						max = 0
					}
					--[[table: 0x83df0b0]]
				}
				--[[table: 0x83bbce0]]
			}
			--[[table: 0x84c60d0]],
			sourcerange = {
				min = 1,
				max = 10000
			}
			--[[table: 0x84c6158]],
			content = {
				nil --[[ref]],
				nil --[[ref]]
			}
			--[[table: 0x84c6068]],
			tag = "MBlock"
		}
		--[[table: 0x83d3a48]],
		tag = "MInternalContent"
	}
	--[[table: 0x83d3968]];
	_.unknownglobalvars[1].occurrences[1].definition = _.unknownglobalvars[1];
	_.content.localvars[1].item.type.expression.left = _.unknownglobalvars[1].occurrences[1];
	_.content.localvars[1].item.occurrences[1].definition = _.content.localvars[1].item;
	_.content.content[1] = _.content.localvars[1].item.type.expression;
	_.content.content[2] = _.content.localvars[1].item.occurrences[1];
	return _;
end