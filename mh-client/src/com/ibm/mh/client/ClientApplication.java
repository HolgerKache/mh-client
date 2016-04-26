package com.ibm.mh.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.BasicConfigurator;

/**
 * Servlet implementation class ClientApplication
 */
@WebServlet("/ClientApplication")
public class ClientApplication extends HttpServlet {
	private static final long serialVersionUID = 1L;
	

	Properties props = new Properties();
	
	// The credentials are passed with the request but don't get
	// propagated to the CredentialProvide using the given jaasLoginModule
	// in server.xml
	static String username=null;
	static String password=null;
	
	// These values are passed by the request and work fine
	String brokerUrl = "https://kafka01-prod01.messagehub.services.us-south.bluemix.net:9093";
	String topic = "livechat";
	
	String clientId = null;
	
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClientApplication() {
        super();
        BasicConfigurator.configure();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		// The request _owns_ the credentials (username and password)
		// Outside the context of this request the credentials are NULL
		
		username=request.getParameter("username");
		password=request.getParameter("username");
		
		try {
			KafkaProducer<String, String> producer = 
					new KafkaProducer<String, String>(getProducerProperties("KafkaClient"));
			
			for (int i = 0; i < 10; i++) {
				send(producer, String.valueOf(i), new String("Message " + i));
			}
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			
			if (e.getCause() != null) {
				System.out.println(e.getCause().getMessage());
			}
		}

		doGet(request, response);
	}

	/**
	 * @param clientId
	 * @return
	 */
	public Properties getProducerProperties(String clientId){
		
		props.setProperty("bootstrap.servers", brokerUrl);
		
		props.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		
		props.setProperty("serializer.class", "kafka.serializer.StringEncoder");
		
		props.setProperty("client.id", clientId);
		props.setProperty("metadata.max.age.ms", "60000");
		
		props.setProperty("security.protocol", "SASL_SSL");
		
		props.setProperty("ssl.protocol", "TLSv1.2");
		props.setProperty("ssl.enabled.protocols", "TLSv1.2");
		
		//  props.setProperty("sasl.kerberos.service.name", "KafkaClient");
	
		return props;
	}
	
    /**
     * @param kafkaProducer
     * @param key
     * @param message
     * @throws UnsupportedEncodingException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public void send(KafkaProducer<String, String> kafkaProducer, final String key, String message) 
    		throws UnsupportedEncodingException, InterruptedException, ExecutionException {
      
    	// Create a producer record which will be sent
        // to the Message Hub service, providing the topic
        // name, field name and message. The field name and
        // message are converted to UTF-8.
    	RecordMetadata m = null;
    	
        ProducerRecord<String, String> record;
		try {		
			record = new ProducerRecord<String, String>(
					topic, key, message);
		
	        System.out.println(" >>>> sending " +
         		   " document_id " + key);
	 
	         
	 	     // Asynchronous response from Message Hub / Kafka.
		     kafkaProducer.send(record,
	        		new Callback() {
	        	public void onCompletion(RecordMetadata m, Exception e) {
	        		if(e != null) {
	        			e.printStackTrace();
	        		} else { 
	        			 System.out.println(" **** Message sent, offset: " + m.offset() + 
	        					" @ partition " + m.partition());
	        			 System.out.println(" <<<< " +
	        					" document_id " + key);
	        		}
	        	}
	        });
	

		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
}
