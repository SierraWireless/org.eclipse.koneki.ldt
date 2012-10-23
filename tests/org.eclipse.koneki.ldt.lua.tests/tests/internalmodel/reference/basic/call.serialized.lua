do local _ = {
		unknownglobalvars = {
			{
				name = "call",
				shortdescription = "",
				description = "",
				occurrences = {
					{
						sourcerange = {
							min = 11,
							max = 14
						}
						--[[table: 0x83d4738]],
						definition = nil --[[ref]],
						tag = "MIdentifier"
					}
					--[[table: 0x83d4710]]
				}
				--[[table: 0x83d61c8]],
				sourcerange = {
					min = 11,
					max = 14
				}
				--[[table: 0x83d61f0]],
				tag = "item"
			}
			--[[table: 0x83d60b8]]
		}
		--[[table: 0x83d3db8]],
		content = {
			localvars = {
				{
					item = {
						type = {
							expression = {
								sourcerange = {
									min = 11,
									max = 19
								}
								--[[table: 0x83d4880]],
								func = nil --[[ref]],
								tag = "MCall"
							}
							--[[table: 0x83d47e0]],
							returnposition = 1,
							tag = "exprtyperef"
						}
						--[[table: 0x83d5f10]],
						shortdescription = "",
						name = "c",
						occurrences = {
							{
								sourcerange = {
									min = 7,
									max = 7
								}
								--[[table: 0x83d5148]],
								definition = nil --[[ref]],
								tag = "MIdentifier"
							}
							--[[table: 0x83d46b8]]
						}
						--[[table: 0x83d5e80]],
						sourcerange = {
							min = 7,
							max = 7
						}
						--[[table: 0x83d5ea8]],
						description = "",
						tag = "item"
					}
					--[[table: 0x83d5d70]],
					scope = {
						min = 0,
						max = 0
					}
					--[[table: 0x83d6050]]
				}
				--[[table: 0x83d6028]]
			}
			--[[table: 0x83d3ea8]],
			sourcerange = {
				min = 1,
				max = 10000
			}
			--[[table: 0x83d3ed0]],
			content = {
				nil --[[ref]],
				nil --[[ref]]
			}
			--[[table: 0x83d3e80]],
			tag = "MBlock"
		}
		--[[table: 0x83d3de0]],
		tag = "MInternalContent"
	}
	--[[table: 0x83d3d18]];
	_.unknownglobalvars[1].occurrences[1].definition = _.unknownglobalvars[1];
	_.content.localvars[1].item.type.expression.func = _.unknownglobalvars[1].occurrences[1];
	_.content.localvars[1].item.occurrences[1].definition = _.content.localvars[1].item;
	_.content.content[1] = _.content.localvars[1].item.type.expression;
	_.content.content[2] = _.content.localvars[1].item.occurrences[1];
	return _;
end