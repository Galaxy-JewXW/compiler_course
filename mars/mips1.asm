.data



.text
main:
main_b0:
# %1 = call i32 @getint()
li $v0, 5
syscall
move $t0, $v0
# %2 = icmp sgt i32 %1, 0
# br i1 %2, label %3, label %7
bgt $t0, 0, main_b3
j main_b7
main_b3:
# %4 = trunc i32 -156 to i8
li $k0, -156
andi $t0, $k0, 255
# move %4 -> %7
move $t0, $t0
# br label %10
j main_b10
main_b7:
# move 101 -> %7
li $t0, 101
# br label %10
j main_b10
main_b10:
# %11 = trunc i8 %7 to i8
move $t0, $t0
# call void @putch(i8 %11)
move $a0, $t0
li $v0, 11
syscall
# ret i32 0
li $v0, 10
syscall
