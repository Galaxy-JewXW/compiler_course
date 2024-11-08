.data:

.text:
func_main:
main_bb0:
    # move 0 -> %v5
    li $t2, 0
    # br label %b6
    j main_bb6

main_bb2:
    # br label %b3

main_bb3:
    # %v4 = add i32 %v5, 1
    addiu $t0, $t2, 1
    # move %v4 -> %v5
    move $t2, $t0
    # move 27 -> %v6
    li $t1, 27
    # move 9 -> %v7
    li $t0, 9
    # move 3 -> %v8
    li $t4, 3
    # br label %b6

main_bb6:
    # %v7 = icmp slt i32 %v5, 1000
    # br i1 %v7, label %b2, label %b8
    blt $t2, 1000, main_bb2

main_bb8:
    # call void @putint(i32 %v8)
    move $a0, $t4
    li $v0, 1
    syscall
    # call void @putint(i32 %v7)
    move $a0, $t0
    syscall
    # call void @putint(i32 %v6)
    move $a0, $t1
    syscall
    # ret i32 0
    li $v0, 10
    syscall

