package main;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class servidor {
    
    public static void main(String[] args) {
        
        MarcoServidor marco = new MarcoServidor();
        
        marco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
}


class MarcoServidor extends JFrame implements Runnable{
    
    public MarcoServidor(){
        
        setBounds(800,300,280,350);
        
        JPanel miLamina = new JPanel();
        
        miLamina.setLayout(new BorderLayout());
        
        areatexto = new JTextArea();
        
        miLamina.add(areatexto, BorderLayout.CENTER);
        
        add(miLamina);
        
        setVisible(true);
        
        Thread mihilo = new Thread(this);
        
        mihilo.start();
    }
    
    private JTextArea areatexto;

    @Override
    public void run() {
            
        try {
        
            ServerSocket servidor = new ServerSocket(9999);
            
            String nick, ip, mensaje;
            
            ArrayList <String> listaIp = new ArrayList<String>();
            
            PaqueteEnvio paquete_recibido;
            
            while (true) {
                
                Socket misocket = servidor.accept();        
                
                ObjectInputStream paquete_datos = new ObjectInputStream(misocket.getInputStream());
                
                paquete_recibido = (PaqueteEnvio) paquete_datos.readObject();
                
                nick = paquete_recibido.getNick();
                
                ip = paquete_recibido.getIp();
                
                mensaje = paquete_recibido.getMensaje();
                
                if(!mensaje.equals("Online")) {
                	
                	areatexto.append("\n" + nick + ": " + mensaje + " para " + ip);
                    
                    
                }else {
             /*  -------------------- DETECTOR USUARISO ONLINE START --------------------  */        
                    
                    InetAddress localizacion = misocket.getInetAddress();
                    
                    String IpRemota = localizacion.getHostAddress();
                    
                    // System.out.println("Online " + IpRemota);  <--  Línea usada para comprobar que se obtenia la ip del cliente al abrir su ventana
                    
                    listaIp.add(IpRemota);
                    
                    paquete_recibido.setIps(listaIp);
                    
                    for(String i:listaIp) {
                    	
                    	System.out.println("Array: " + i);
                    	
                    	Socket enviaDestinatario = new Socket(i, 9090);
                        
                        ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
                        
                        paqueteReenvio.writeObject(paquete_recibido);
                        
                        paqueteReenvio.close();
                        
                        enviaDestinatario.close();
                        
                        misocket.close();
                    }
                    
             /*  -------------------- DETECTOR USUARISO ONLINE END --------------------  */
                }
            }
            
        } catch (IOException | ClassNotFoundException ex) {
            
            Logger.getLogger(MarcoServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}