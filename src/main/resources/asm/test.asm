.text
main: addi $1, $0, 1999
noop
halt
sw $r18, -640($r7)
add $2, $1, $0
add $r3, $r2, $r1
bgt $0, $1, main
addi $4, $6, -120
addi $5, $3, 100
bne $3, $2, middle
addi $6, $1, 65535
addi $7, $1, -65536
middle:
sub $8, $2, $1
and $9, $2, $1
inline: or $9, $2, $1
addi $7, $1, -65536
sll $10, $2, 31
sw $3, 0($0)
lw $16, 0($0)
sra $11, $2, 0
end:
jr $0
anotherlabel: lw $r3, heapsize($r0)
lw $r4, wow($r0)
mul $r5, $r3, $r4
addi $r7, $r0, 0x0000DF00
j quit
dead: addi $r7, $r0, 0x0000DF00
quit:

.data
wow: .word 0x0000B504
mystring: .string ASDASDASDASDASDASD
var: .char Z
label: .char A
heapsize: .word 0x00000000
myheap: .word 0x00000000