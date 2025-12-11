# Sus Assembly Test 2: check medbay bit, eject player if not confirmed

# medbay word assumed at 0xFFFF0000 (used by scanmed)
# bit 0 = 1 -> confirmed crew
# bit 0 = 0 -> not confirmed

.text
.globl main

main:
    li  $s0, 2                # $s0 = player index (crew A)
    li  $t0, 1                # mask 0x1

    scanmed  $t1              # t1 = medbay status word
    taskand  $t1,$t1,$t0      # t1 = t1 & 1 (keep bit 0)

    susbeq   $t1,$zero, maybe_sus   # if bit 0 == 0 -> not confirmed

    vent      done            # confirmed -> nothing happens

maybe_sus:
    voteout   $s0             # mark player as ejected
    clearvotes                # reset vote counters (t0–t3)

done:
    vent      done