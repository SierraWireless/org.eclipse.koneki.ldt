do local _ = {
		unknownglobalvars = {
			{
				name = "file",
				shortdescription = "",
				description = "",
				occurrences = {
					{
						sourcerange = {
							min = 11,
							max = 14
						}
						--[[table: 0x8571b10]],
						definition = nil --[[ref]],
						tag = "MIdentifier"
					}
					--[[table: 0x84f1030]]
				}
				--[[table: 0x85197a0]],
				sourcerange = {
					min = 11,
					max = 14
				}
				--[[table: 0x8492118]],
				tag = "item"
			}
			--[[table: 0x8519778]]
		}
		--[[table: 0x83bde90]],
		content = {
			localvars = {
				{
					item = {
						type = {
							expression = {
								record = nil --[[ref]],
								sourcerange = {
									min = 11,
									max = 21
								}
								--[[table: 0x84ab2c0]],
								tag = "MInvoke",
								functionname = "read"
							}
							--[[table: 0x84f23a0]],
							returnposition = 1,
							tag = "exprtyperef"
						}
						--[[table: 0x84dd558]],
						shortdescription = "",
						name = "f",
						occurrences = {
							{
								sourcerange = {
									min = 7,
									max = 7
								}
								--[[table: 0x848ee50]],
								definition = nil --[[ref]],
								tag = "MIdentifier"
							}
							--[[table: 0x83f0c48]]
						}
						--[[table: 0x84eda48]],
						sourcerange = {
							min = 7,
							max = 7
						}
						--[[table: 0x84dd4f0]],
						description = "",
						tag = "item"
					}
					--[[table: 0x84eda20]],
					scope = {
						min = 0,
						max = 0
					}
					--[[table: 0x8517570]]
				}
				--[[table: 0x8517548]]
			}
			--[[table: 0x84f57a0]],
			sourcerange = {
				min = 1,
				max = 10000
			}
			--[[table: 0x8566450]],
			content = {
				nil --[[ref]],
				nil --[[ref]]
			}
			--[[table: 0x84f5778]],
			tag = "MBlock"
		}
		--[[table: 0x83bdeb8]],
		tag = "MInternalContent"
	}
	--[[table: 0x8463d98]];
	_.unknownglobalvars[1].occurrences[1].definition = _.unknownglobalvars[1];
	_.content.localvars[1].item.type.expression.record = _.unknownglobalvars[1].occurrences[1];
	_.content.localvars[1].item.occurrences[1].definition = _.content.localvars[1].item;
	_.content.content[1] = _.content.localvars[1].item.type.expression;
	_.content.content[2] = _.content.localvars[1].item.occurrences[1];
	return _;
end