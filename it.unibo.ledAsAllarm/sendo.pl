%====================================================================================
% sendo description   
%====================================================================================
mqttBroker("broker.hivemq.com", "1883", "unibo/sonar/events").
context(ctxdummy, "192.168.1.14",  "TCP", "8068").
 qactor( sendo, ctxdummy, "it.unibo.sendo.Sendo").
