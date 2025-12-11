# Sus Assembly Test 3: random task, fake task, emergency meeting if suspicion < 0

.text
.globl main

main:
    li  $t0, -1               # suspicion meter (negative = danger)

    randomtask $t1            # random task id
    faketask  $t2,$t1         # copy task, set susFlag = 1

    ventifneg $t0, call_meeting   # if suspicion < 0 → meeting
    vent       done               # otherwise end

call_meeting:
    emergency  meeting_handler    # save $ra, jump to handler
    vent       done               # (fallback, usually we return)

meeting_handler:
    clearvotes                    # reset votes
    checksus $t1,$t2              # re-check susFlag based on t1,t2
    jr   $ra                      # return to caller (standard MIPS)