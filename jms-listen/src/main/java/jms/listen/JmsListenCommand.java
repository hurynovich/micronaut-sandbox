package jms.listen;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import javax.jms.*;

@Command(name = "jms-listen", description = "...", mixinStandardHelpOptions = true)
public class JmsListenCommand implements Runnable {
  private static Logger log = LoggerFactory.getLogger(JmsListenCommand.class);

  @Option(names = {"-v", "--verbose"}, description = "...")
  boolean verbose;

  public static void main(String[] args) throws Exception {
    PicocliRunner.run(JmsListenCommand.class, args);
  }

  public void run() {

    var connFactory = new ActiveMQConnectionFactory();
    connFactory.setBrokerURL("tcp://localhost:61616");
    connFactory.setUserName("admin");
    connFactory.setPassword("admin");
    connFactory.setClientIDPrefix("micronaut-");
    try {
      Connection conn = connFactory.createConnection();
      Session s1 = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
      var t1 = s1.createTopic("VirtualTopic.hello.jms");
      var c1 = s1.createConsumer(t1);
      var c2 = s1.createConsumer(t1);
      c1.setMessageListener(msg -> log.info("MSG: {}", msg));
      c2.setMessageListener(msg -> log.info("MSG: {}", msg));
      conn.start();
      log.info("Connection was started.");
    } catch (JMSException e) {
      throw new RuntimeException(e);
    }
  }
}
