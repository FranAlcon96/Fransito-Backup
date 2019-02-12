package app.backup;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JTextField;

public class FransitoBackup extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JTextField txtRuta;
	private JButton btnRuta,btnComprobar,btnBackup;
	private File fichero;
	private ArrayList<File> ficheros1 = new ArrayList<File>();
	private ArrayList<File> ficheros2 = new ArrayList<File>();
	private ArrayList<String> SHA256Lista;
	private ArrayList<String> f1 = new ArrayList<String>();
	private ArrayList<String> f2 = new ArrayList<String>();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FransitoBackup frame = new FransitoBackup();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FransitoBackup() {
		setTitle("Fransito Backup");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 448, 353);
		contentPane = new JPanel();
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblRuta = new JLabel("Seleccione una ruta:");
		lblRuta.setForeground(Color.WHITE);
		lblRuta.setFont(new Font("Myriad Pro", Font.PLAIN, 16));
		lblRuta.setBounds(136, 24, 135, 22);
		contentPane.add(lblRuta);
		
		btnRuta = new JButton("Ruta");
		btnRuta.setFont(new Font("Myriad Pro", Font.PLAIN, 14));
		btnRuta.setBounds(136, 69, 144, 35);
		contentPane.add(btnRuta);
		btnRuta.addActionListener(this);
		
		txtRuta = new JTextField();
		txtRuta.setFont(new Font("Myriad Pro", Font.PLAIN, 14));
		txtRuta.setEditable(false);
		txtRuta.setBounds(30, 144, 370, 42);
		contentPane.add(txtRuta);
		txtRuta.setColumns(10);
		
		btnComprobar = new JButton("Comprobar");
		btnComprobar.setFont(new Font("Myriad Pro", Font.PLAIN, 14));
		btnComprobar.setBounds(30, 243, 135, 42);
		contentPane.add(btnComprobar);
		btnComprobar.addActionListener(this);
		
		btnBackup = new JButton("Backup");
		btnBackup.setEnabled(false);
		btnBackup.setFont(new Font("Myriad Pro", Font.PLAIN, 14));
		btnBackup.setBounds(265, 243, 135, 42);
		contentPane.add(btnBackup);
		btnBackup.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnRuta)) {
			JFileChooser fc=new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int seleccion=fc.showOpenDialog(contentPane);
			 
			if(seleccion==JFileChooser.APPROVE_OPTION){
				fichero=fc.getSelectedFile();
				ficheros1.clear();
				add(fichero,ficheros1);
				f1.clear();
				try {
					f1 = ObtenerMD5(ficheros1);
				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    txtRuta.setText(fichero.getAbsolutePath());
			}
		}
		
		if (e.getSource().equals(btnComprobar)) {
			
			if (txtRuta.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Por favor, seleccione una ruta", "Fallo",JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			
			ficheros2.clear();
			add(fichero,ficheros2);
			f2.clear();
			try {
				f2 = ObtenerMD5(ficheros2);
			} catch (NoSuchAlgorithmException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			try {
				if (comprobar()) {
					btnBackup.setEnabled(true);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
						
		}
		
		if(e.getSource().equals(btnBackup)) {
			JFileChooser fc=new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setDialogTitle("¿ Donde quieres guardar el backup ?");
			int seleccion=fc.showSaveDialog(contentPane);
			 
			if(seleccion==JFileChooser.APPROVE_OPTION){
				//comprimir(fichero.getAbsolutePath(), fc.getSelectedFile().getAbsolutePath()+"\\"+fichero.getName()+".zip");
				if (fichero.isFile()) {
					try {
						FileInputStream in = new FileInputStream(fichero.getPath());
						FileOutputStream out = new FileOutputStream(fc.getSelectedFile()+"\\"+fichero.getName()+".zip");
						byte[] b = new byte[4096];
						ZipOutputStream zipOut = new ZipOutputStream(out);
						ZipEntry entry = new ZipEntry(fichero.getName());
						zipOut.putNextEntry(entry);
						int len = 0;
						while ((len = in.read(b)) != -1) {
							zipOut.write(b, 0 ,len);
						}
						zipOut.closeEntry();
						zipOut.close();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					copiarArchivo(fichero, fc.getSelectedFile().getAbsolutePath()+"\\");
					JOptionPane.showMessageDialog(null, "Backup realizado", "Mensaje",JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				
				if (fichero.isDirectory()) {
					comprimir(fichero.getAbsolutePath(), fc.getSelectedFile().getAbsolutePath()+"\\"+fichero.getName()+".zip");
					copiarDirectorio(fichero, fc.getSelectedFile().getAbsolutePath()+"\\");
					JOptionPane.showMessageDialog(null, "Backup realizado", "Mensaje",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}
	
	private boolean comprobar() throws NoSuchAlgorithmException {
		if (ficheros1.size() != ficheros2.size()) {
			JOptionPane.showMessageDialog(null, "El número de archivos es distinto por lo que se ha realizado una modificación", "Warning",JOptionPane.WARNING_MESSAGE);			
			return true;
		}
		
		for (int i = 0; i < f1.size(); i++) {
			
			if (!f1.get(i).equals(f2.get(i))) {
			JOptionPane.showMessageDialog(null, "Se ha detectado una modificación en un fichero", "Warning",JOptionPane.WARNING_MESSAGE);			
			return true;
			}
			
		}
		
		for (int i = 0; i < ficheros1.size(); i++) {
			
			
			System.out.println(ficheros1.get(i).getAbsolutePath() +" - "+ficheros2.get(i).getAbsolutePath());
			
			if (!ficheros1.get(i).getName().equals(ficheros2.get(i).getName())) {
				fichero = ficheros1.get(i);
				JOptionPane.showMessageDialog(null, "El nombre de un archivo ha sido modificado ", "Warning",JOptionPane.WARNING_MESSAGE);
				return true;
			}
			
			if (ficheros1.get(i).length()!=ficheros2.get(i).length()) {
				JOptionPane.showMessageDialog(null, "El tamaño de un archivo de un archivo ha sido modificado ", "Warning",JOptionPane.WARNING_MESSAGE);
				return true;				
			}
		}
		return false;
	}
	
 	//Método que añade al array al poner la ruta
 	private boolean add(File entrada, ArrayList<File> archivos){
 		 
		if (!entrada.exists()){ 
			JOptionPane.showMessageDialog(null, entrada.getName()+" no existe", "Fallo",JOptionPane.ERROR_MESSAGE);
			btnBackup.setEnabled(false);
	    }else if (entrada.isFile()) { 
	    	archivos.add(entrada);
	    	return false;
	    } else if (entrada.isDirectory()){
	    	archivos.add(entrada);
	    	File[] files = entrada.listFiles();
           	if (files.length > 0) 
           		{	           		
           		for (File f : files) {
           			//archivos.add(f);
				    add(f,archivos);  
           			}
           		} 
	    }
		return false;
    }

 	public void comprimir(String archivo, String archivoZIP) {
        try {
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(archivoZIP));
            agregarCarpeta("", archivo, zip);
            zip.flush();
            zip.close();
        } catch (IOException ex) {
        	JOptionPane.showMessageDialog(null, "Error de entrada y salida "+ex.getMessage(), "Fallo",JOptionPane.ERROR_MESSAGE); 
        } catch (Exception ex1) {
        	JOptionPane.showMessageDialog(null, "Error en la compresión "+ex1.getMessage(), "Fallo",JOptionPane.ERROR_MESSAGE); 
        }
    }

    public void agregarCarpeta(String ruta, String carpeta, ZipOutputStream zip) throws Exception {
        File directorio = new File(carpeta);
        for (String nombreArchivo : directorio.list()) {
            if (ruta.equals("")) {
                agregarArchivo(directorio.getName(), carpeta + "/" + nombreArchivo, zip);
            } else {
                agregarArchivo(ruta + "/" + directorio.getName(), carpeta + "/" + nombreArchivo, zip);
            }
        }
    }

    public void agregarArchivo(String ruta, String directorio, ZipOutputStream zip) throws Exception {
        File archivo = new File(directorio);
        if (archivo.isDirectory()) {
            agregarCarpeta(ruta, directorio, zip);
        } else {
            byte[] buffer = new byte[4096];
            int leido;
            FileInputStream entrada = new FileInputStream(archivo);
            zip.putNextEntry(new ZipEntry(ruta + "/" + archivo.getName()));
            while ((leido = entrada.read(buffer)) > 0) {
                zip.write(buffer, 0, leido);
            }
        }
    }
    
    private void copiarArchivo(File archivo, String dirDestino)
	{
		String nombre = archivo.getName();
		File archDestino = new File(dirDestino + nombre);
		
		try 
		{
			archDestino.createNewFile();
			
			InputStream in = new FileInputStream(archivo);
			OutputStream out = new FileOutputStream(archDestino);
			
			byte[] buffer = new byte[1024];
			int len;
			while( (len=in.read(buffer)) > 0 )
			{
				out.write(buffer, 0, len);
			}
			in.close();
			out.close();
		} 
		catch(Exception err)
		{
			
		}
	}
    

	private void copiarDirectorio(File directorio, String dirDestino)
	{
		String dirNombre = directorio.getName();
		File archDestino = new File(dirDestino + dirNombre);
		
		archDestino.mkdir();
		File [] porCopiar = directorio.listFiles();
		
		for(File file : porCopiar)
		{
			if(file.isDirectory())
			{
				copiarDirectorio(file, archDestino.getAbsolutePath() + "\\");
			}
			else
			{
				copiarArchivo(file, archDestino.getAbsolutePath() + "\\");
			}
		}
	}
	
	public ArrayList<String> ObtenerMD5(ArrayList<File> lista) throws NoSuchAlgorithmException {
        SHA256Lista = new ArrayList<>();

        for (int i = 0; i < lista.size(); i++) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            if(lista.get(i).isFile()){
                try (DigestInputStream dis = new DigestInputStream(new FileInputStream(lista.get(i)), md)) {
                while (dis.read() != -1) ; //empty loop to clear the data
                md = dis.getMessageDigest();
            } catch (IOException ex) {
                //Logger.getLogger(copia.class.getName()).log(Level.SEVERE, null, ex);
            }
            StringBuilder result = new StringBuilder();
            for (byte b : md.digest()) {
                result.append(String.format("%02x", b));
            }
            SHA256Lista.add(result.toString());
            }
        }

        return SHA256Lista;
    }	
	
}
