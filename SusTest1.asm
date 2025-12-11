# Sus Assembly Test 1: compare the two reports and determine if sus or not

.text
.globl main

main:
    li $t0, 5                 # first report value
    li $t1, 5                 # second report value

    checksus $t0,$t1          # susFlag = (t0 != t1)
    susbeq   $t0,$t1, not_sus # if t0 == t1 -> branch

    # values different path
    report   $t0              # nonzero -> susFlag = 1
    vent     done

not_sus:
    report   $zero            # 0 -> susFlag = 0

done:
    vent     done             # idle loop