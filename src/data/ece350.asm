.text
main: addi $1, $0, 1999
noop
halt
sw $18, -640($7)
add $2, $1, $0
add $3, $2, $1
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
anotherlabel: lw $3, heapsize($0)
lw $4, wow($0)
mul $5, $3, $4
addi $7, $0, 0x0000DF00
j quit
dead: addi $7, $0, 0x0000DF00
quit:
j quit

.data
wow: .word 0x0000B504
mystring: .string ASDASDASDASDASDASD
var: .char Z
label: .char A
heapsize: .word 0x00000000
myheap: .word 0x00000000