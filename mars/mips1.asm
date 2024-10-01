.data
s0: .asciiz "--1"
s1: .asciiz "--2"
s2: .asciiz "--3"


.text
main:
main_b0:
# br label %2
j main_b2
main_b1:
# call void @putint(i32 4)
li $a0, 4
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.s.0, i64 0, i64 0))
la $a0, s0
li $v0, 4
syscall
# call void @putint(i32 4)
li $a0, 4
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.s.1, i64 0, i64 0))
la $a0, s1
li $v0, 4
syscall
# br label %4
j main_b4
main_b2:
# br label %1
j main_b1
main_b3:
# call void @putint(i32 4)
li $a0, 4
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.s.2, i64 0, i64 0))
la $a0, s2
li $v0, 4
syscall
# ret i32 0
li $v0, 10
syscall
main_b4:
# br label %5
j main_b5
main_b5:
# br label %3
j main_b3
