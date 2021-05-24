%====================================================================================
% callersealone description   
%====================================================================================
context(ctxcallers, "localhost",  "TCP", "8050").
context(ctxsonarobserver, "localhost",  "TCP", "8083").
 qactor( caller2, ctxcallers, "it.unibo.caller2.Caller2").
  qactor( led, ctxcallers, "it.unibo.led.Led").
msglogging.
