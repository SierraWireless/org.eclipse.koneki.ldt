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
				--[[table: 007F6D58]],
				occurrences={
					[1]={
						sourcerange={
							min=1,
							max=1
						}
						--[[table: 007F5598]],
						definition=nil --[[ref]],
						tag="MIdentifier"
					}
					--[[table: 007F54A8]],
					[2]={
						sourcerange={
							min=5,
							max=5
						}
						--[[table: 007F9D78]],
						definition=nil --[[ref]],
						tag="MIdentifier"
					}
					--[[table: 007F47B0]]
				}
				--[[table: 007F6BC8]],
				tag="item"
			}
			--[[table: 007F6BA0]]
		}
		--[[table: 00801F28]],
		content={
			localvars={

			}
			--[[table: 007F6FD8]],
			sourcerange={
				min=1,
				max=10000
			}
			--[[table: 007F71B8]],
			content={
				[1]={
					sourcerange={
						min=1,
						max=3
					}
					--[[table: 007F5B60]],
					func=nil --[[ref]],
					tag="MCall"
				}
				--[[table: 007F5A98]],
				[2]=nil --[[ref]]
			}
			--[[table: 00801F00]],
			tag="MBlock"
		}
		--[[table: 00801D48]],
		tag="MInternalContent"
	}
	--[[table: 00801D70]];
	_.unknownglobalvars[1].occurrences[1].definition=_.unknownglobalvars[1];
	_.unknownglobalvars[1].occurrences[2].definition=_.unknownglobalvars[1];
	_.content.content[1].func=_.unknownglobalvars[1].occurrences[1];
	_.content.content[2]=_.unknownglobalvars[1].occurrences[2];
	return _;
end