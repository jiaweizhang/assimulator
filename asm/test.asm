main:
add $2, $1, $0
add $r3, $r2, $r0
addi $5, $3, 100
bne $5, $4, middle
addi $6, $1, 65535
addi $7, $1, -65536
middle:
sub $8, $2, $1
and $9, $2, $1
inline: or $9, $2, $1
sll $10, $2, 31
sra $11, $2, 0
end:
jr $0