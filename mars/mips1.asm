.data:

.text:
func_main:
main_bb0:
    # %v1 = call i32 @getint()
    li $v0, 5
    syscall
    move $t1, $v0
    # %v2 = call i32 @getint()
    li $v0, 5
    syscall
    move $t0, $v0
    # %v3 = and i32 %v1, %v2
    and $t0, $t1, $t0
    # call void @putint(i32 %v3)
    move $a0, $t0
    li $v0, 1
    syscall
    # ret i32 0
    li $v0, 10
    syscall

