.data:

.text:
func_main:
main_bb0:
    # %v1 = call i32 @getint()
    li $v0, 5
    syscall
    move $t0, $v0
    # %v2 = sfuck i32 %v1, 2
    addiu $t0, $t0, 2
    move $k0, $t0
    mul $t0, $k0, $t0
    # %v3 = add i32 %v2, 1296
    addiu $t0, $t0, 1296
    # call void @putint(i32 %v3)
    move $a0, $t0
    li $v0, 1
    syscall
    # ret i32 0

