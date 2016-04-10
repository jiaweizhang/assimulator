main: addi $1, $0, 1999
add $2, $1, $0
add $r3, $r2, $r1
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