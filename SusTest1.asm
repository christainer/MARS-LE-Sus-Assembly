# Sus Assembly Test 1: compare the two reports and determine if sus or not. 1 = sus, 1 = not sus

.text
.globl main

main:
    taskset  $t0, $zero, 4                 # first report value
    taskset  $t1, $zero, 5                 # second report value

    checksus $t0,$t1          # susFlag = (t0 != t1)
    susbeq   $t0,$t1, not_sus # if t0 == t1 -> branch

    # values different path
    report   $t0              # nonzero -> susFlag = 1
    suspeek  $t2
    vent     done

not_sus:
    report   $zero            # 0 -> susFlag = 0
    suspeek  $t2
    vent done

done:

