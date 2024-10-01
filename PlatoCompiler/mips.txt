.data
s0: .asciiz "a = "
s1: .asciiz ", b = "
s2: .asciiz "\n"
s3: .asciiz "b = "


.text
main:
main_b0:
# %1 = zext i8 2 to i32
li $k0, 2
move $t0, $k0
# call void @putstr(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @.s.0, i64 0, i64 0))
la $a0, s0
li $v0, 4
syscall
# call void @putint(i32 255)
li $a0, 255
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([7 x i8], [7 x i8]* @.s.1, i64 0, i64 0))
la $a0, s1
li $v0, 4
syscall
# call void @putint(i32 %1)
move $a0, $t0
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.2, i64 0, i64 0))
la $a0, s2
li $v0, 4
syscall
# %2 = zext i8 2 to i32
li $k0, 2
move $t0, $k0
# %3 = add i32 255, %2
addiu $t0, $t0, 255
# %4 = trunc i32 %3 to i8
andi $t0, $t0, 255
# %5 = zext i8 %4 to i32
move $t0, $t0
# call void @putstr(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @.s.3, i64 0, i64 0))
la $a0, s3
li $v0, 4
syscall
# call void @putint(i32 %5)
move $a0, $t0
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.2, i64 0, i64 0))
la $a0, s2
li $v0, 4
syscall
# ret i32 0
li $v0, 10
syscall
