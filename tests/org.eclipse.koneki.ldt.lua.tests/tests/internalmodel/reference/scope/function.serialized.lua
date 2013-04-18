do local _={
		unknownglobalvars={
			[1]={
				description="",
				shortdescription="",
				name="d",
				sourcerange={
					min=31,
					max=31
				}
				--[[table: 00A3D370]],
				occurrences={
					[1]={
						sourcerange={
							min=31,
							max=31
						}
						--[[table: 00A3B778]],
						definition=nil --[[ref]],
						tag="MIdentifier"
					}
					--[[table: 00A3A4E0]]
				}
				--[[table: 00A3C0D8]],
				tag="item"
			}
			--[[table: 00A3C100]],
			[2]={
				description="",
				shortdescription="",
				name="f",
				sourcerange={
					min=1,
					max=1
				}
				--[[table: 00A3D460]],
				occurrences={
					[1]={
						sourcerange={
							min=1,
							max=1
						}
						--[[table: 00A3ABE8]],
						definition=nil --[[ref]],
						tag="MIdentifier"
					}
					--[[table: 00A3AC10]],
					[2]={
						sourcerange={
							min=14,
							max=14
						}
						--[[table: 00A3B3E0]],
						definition=nil --[[ref]],
						tag="MIdentifier"
					}
					--[[table: 00A3AF30]]
				}
				--[[table: 00A3D438]],
				tag="item"
			}
			--[[table: 00A3D398]]
		}
		--[[table: 00A3AAD0]],
		content={
			localvars={

			}
			--[[table: 00A3AD50]],
			sourcerange={
				min=1,
				max=10000
			}
			--[[table: 00A3ADF0]],
			content={
				[1]={
					sourcerange={
						min=1,
						max=3
					}
					--[[table: 00A3B160]],
					func=nil --[[ref]],
					tag="MCall"
				}
				--[[table: 00A3B0C0]],
				[2]=nil --[[ref]],
				[3]={
					localvars={
						[1]={
							item={
								description="",
								shortdescription="",
								name="d",
								sourcerange={
									min=25,
									max=25
								}
								--[[table: 00A3D230]],
								occurrences={
									[1]={
										sourcerange={
											min=25,
											max=25
										}
										--[[table: 00A3A508]],
										definition=nil --[[ref]],
										tag="MIdentifier"
									}
									--[[table: 00A3A530]]
								}
								--[[table: 00A3D258]],
								tag="item"
							}
							--[[table: 00A3D348]],
							scope={
								min=0,
								max=0
							}
							--[[table: 00A3C128]]
						}
						--[[table: 00A3D208]]
					}
					--[[table: 00A3B520]],
					sourcerange={
						min=15,
						max=29
					}
					--[[table: 00A3B548]],
					content={
						[1]=nil --[[ref]]
					}
					--[[table: 00A3B4F8]],
					tag="MBlock"
				}
				--[[table: 00A3B4D0]],
				[4]={
					sourcerange={
						min=31,
						max=33
					}
					--[[table: 00A3B840]],
					func=nil --[[ref]],
					tag="MCall"
				}
				--[[table: 00A3B7A0]]
			}
			--[[table: 00A3AB20]],
			tag="MBlock"
		}
		--[[table: 00A3AAF8]],
		tag="MInternalContent"
	}
	--[[table: 00A3AAA8]];
	_.unknownglobalvars[1].occurrences[1].definition=_.unknownglobalvars[1];
	_.unknownglobalvars[2].occurrences[1].definition=_.unknownglobalvars[2];
	_.unknownglobalvars[2].occurrences[2].definition=_.unknownglobalvars[2];
	_.content.content[1].func=_.unknownglobalvars[2].occurrences[1];
	_.content.content[2]=_.unknownglobalvars[2].occurrences[2];
	_.content.content[3].localvars[1].item.occurrences[1].definition=_.content.content[3].localvars[1].item;
	_.content.content[3].content[1]=_.content.content[3].localvars[1].item.occurrences[1];
	_.content.content[4].func=_.unknownglobalvars[1].occurrences[1];
	return _;
end