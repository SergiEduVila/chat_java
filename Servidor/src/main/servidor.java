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
            
            PaqueteEnvio paquete_recibido;
            
            while (true) {
                
                Socket misocket = servidor.accept();        
                
                ObjectInputStream paquete_datos = new ObjectInputStream(misocket.getInputStream());
                
                paquete_recibido = (PaqueteEnvio) paquete_datos.readObject();
                
                nick = paquete_recibido.getNick();
                
                ip = paquete_recibido.getIp();
                
                mensaje = paquete_recibido.getMensaje();
                
                areatexto.append("\n" + nick + ": " + " para" + ip);
                
                Socket enviaDestinatario = new Socket(ip, 9090);
                
                ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
                
                paqueteReenvio.writeObject(paquete_recibido);
                
                paqueteReenvio.close();
                
                enviaDestinatario.close();
                
                misocket.close();
            }
            
        } catch (IOException | ClassNotFoundException ex) {
            
            Logger.getLogger(MarcoServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}