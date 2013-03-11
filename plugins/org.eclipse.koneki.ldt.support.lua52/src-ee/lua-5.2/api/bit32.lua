-------------------------------------------------------------------------------
-- Bitwise Operations.
-- This library provides bitwise operations. It provides all its functions inside the table `bit32`.
-- Unless otherwise stated, all functions accept numeric arguments in the range `(-2^51,+2^51);`
-- each argument is normalized to the remainder of its division by `2^32` and truncated to an integer
-- (in some unspecified way), so that its final value falls in the range `[0,2^32 - 1]`.
-- Similarly, all results are in the range `[0,2^32 - 1]`. Note that `bit32.bnot(0)` is `0xFFFFFFFF`,
-- which is different from `-1`.
-- @module bit32

-------------------------------------------------------------------------------
-- Returns the number `x` shifted `disp` bits to the right. The number `disp` may be any
-- representable integer. Negative displacements shift to the left.
-- 
-- This shift operation is what is called arithmetic shift.
-- Vacant bits on the left are filled with copies of the higher bit of `x`;
-- vacant bits on the right are filled with zeros. In particular, displacements with
-- absolute values higher than 31 result in zero or `0xFFFFFFFF`
-- (all original bits are shifted out).
--
-- @function [parent=#bit32] arshift
-- @param #number x value to shift
-- @param #number disp the number of shift, may an positive or negative integer
-- @return #number shifted number

-------------------------------------------------------------------------------
-- Returns the bitwise `and` of its operands.
-- @function [parent=#bit32] band
-- @param #number ... operands 
-- @return #number bitwise `and` of operands

-------------------------------------------------------------------------------
-- Returns the bitwise negation of `x`. For any integer `x`, the following identity holds:
-- 
--		assert(bit32.bnot(x) == (-1 - x) % 2^32)
--		
-- @function [parent=#bit32] bnot
-- @param #number x Number to proceed
-- @return #number bitwise negation 

-------------------------------------------------------------------------------
-- Returns the bitwise `or` of its operands.
-- @function [parent=#bit32] bor
-- @param #number ... operands 
-- @return #number bitwise `or` of operands

-------------------------------------------------------------------------------
-- Returns a boolean signaling whether the bitwise `and` of its operands is different from zero.
-- @function [parent=#bit32] btest
-- @return #boolean true if the bitwise `and` of its operands is different from zero

-------------------------------------------------------------------------------
-- Returns the bitwise exclusive `or` of its operands.
-- @function [parent=#bit32] bxor
-- @param #number ... operands
-- @return #number bitwise exclusive `or` of its operands.

-------------------------------------------------------------------------------
-- Returns the unsigned number formed by the bits `field` to `field + width - 1` from `n`.
-- Bits are numbered from `0` (least significant) to `31` (most significant).
-- All accessed bits must be in the range `[0, 31]`.
-- 
-- The default for `width` is `1`.
-- 
-- @function [parent=#bit32] extract
-- @param #number n input number
-- @param #number field bit field to apply
-- @param #number width the number of bit to take in account (optional, 1 by default)
-- @return #number extracted number

-------------------------------------------------------------------------------
-- Returns a copy of `n` with the bits `field` to `field + width - 1` replaced by the value `v`.
-- See `bit32.extract` for details about field and width.
-- @function [parent=#bit32] replace
-- @param #number n the number to copy
-- @param #number v the value v
-- @param #number field bits field to apply
-- @param #number width the number of bit to take in account (optional, 1 by default)
-- @return #number replaced number

-------------------------------------------------------------------------------
-- Returns the number `x` rotated `disp` bits to the left. The number `disp` may be
-- any representable integer.
-- For any valid displacement, the following identity holds:
--
--     assert(bit32.lrotate(x, disp) == bit32.lrotate(x, disp % 32))
--
-- In particular, negative displacements rotate to the right.
-- @function [parent=#bit32] lrotate
-- @param #number x original number
-- @param #number disp number of rotate
-- @return #number rotated number

-------------------------------------------------------------------------------
-- Returns the number `x` shifted `disp` bits to the left. The number `disp` may be any representable integer.
-- Negative displacements shift to the right. In any direction, vacant bits are filled with zeros.
-- In particular, displacements with absolute values higher than `31` result in zero (all bits are shifted out).
-- For positive displacements, the following equality holds:
-- 
--      assert(bit32.lshift(b, disp) == (b * 2^disp) % 2^32)
--
-- @function [parent=#bit32] lshift
-- @param #number x original number
-- @param #number disp the number of shift
-- @return #number shifted number

-------------------------------------------------------------------------------
-- Returns the number `x` rotated `disp` bits to the right. The number `disp` may be any representable integer.
-- For any valid displacement, the following identity holds:
-- 
--      assert(bit32.rrotate(x, disp) == bit32.rrotate(x, disp % 32))
--      
-- In particular, negative displacements rotate to the left.
-- @function [parent=#bit32] rrotate
-- @param #number x original number
-- @param #number disp number of bits to rotate
-- @return #number rotated number

-------------------------------------------------------------------------------
-- Returns the number `x` shifted `disp` bits to the right. The number `disp` may be any
-- representable integer. Negative displacements shift to the left. In any direction,
-- vacant bits are filled with zeros. In particular, displacements with absolute
-- values higher than `31` result in zero (all bits are shifted out).
-- 
-- For positive displacements, the following equality holds:
-- 
--      assert(bit32.rshift(b, disp) == math.floor(b % 2^32 / 2^disp))
--      
-- This shift operation is what is called logical shift. 
-- @function [parent=#bit32] rshift
-- @param #number x original number
-- @param #number disp the number of shift
-- @return #number shifted number

return nil
