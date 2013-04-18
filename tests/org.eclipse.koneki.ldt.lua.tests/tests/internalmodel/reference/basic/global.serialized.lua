do local _={
		unknownglobalvars={
			[1]={
				description="",
				shortdescription="",
				name="d",
				sourcerange={
					min=1,
					max=1
				}
				--[[table: 007FEB18]],
				occurrences={
					[1]={
						sourcerange={
							min=1,
							max=1
						}
						--[[table: 00855928]],
						definition=nil --[[ref]],
						tag="MIdentifier"
					}
					--[[table: 008558B0]]
				}
				--[[table: 007FE8C0]],
				tag="item"
			}
			--[[table: 0081D5A8]]
		}
		--[[table: 0085B7D8]],
		content={
			localvars={

			}
			--[[table: 0085BA30]],
			sourcerange={
				min=1,
				max=10000
			}
			--[[table: 0085BA58]],
			content={
				[1]={
					sourcerange={
						min=1,
						max=3
					}
					--[[table: 0084E8D0]],
					func=nil --[[ref]],
					tag="MCall"
				}
				--[[table: 008558D8]]
			}
			--[[table: 0085BA08]],
			tag="MBlock"
		}
		--[[table: 0085B800]],
		tag="MInternalContent"
	}
	--[[table: 0085B6E8]];
	_.unknownglobalvars[1].occurrences[1].definition=_.unknownglobalvars[1];
	_.content.content[1].func=_.unknownglobalvars[1].occurrences[1];
	return _;
end