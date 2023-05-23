package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

class JThread extends Thread
{
    public double top;
    public double low;
    public double step;
    public double result;
    public int strNumber;

    public JThread()
    {
        top = 0;
        low = 0;
        step = 0;
        strNumber = 0;
        result = 0;
    }

    public void setData(double newTop, double newLow, double newStep)
    {
        top=newTop;
        low=newLow;
        step=newStep;
    }

    public double getResult()
    {
        return this.result;
    }

    public void run()
    {
        for (double i = low; i < top; i += step) {
            if(top - i > step)
            {
                result += step * (0.5 * (Math.sin(i) + Math.sin(i + step)));

            }
            else
            {
                result += (top - i) * (0.5 * (Math.sin(i) + Math.sin(i + top)));
            }
        }
    }
}

public class Client
{
    public static void main(String[] args) throws SocketException, IOException {

        DatagramSocket socket = new DatagramSocket(17);
        DatagramSocket socketSend = new DatagramSocket();
        InetAddress address = InetAddress.getByName("localhost");
        String messageSend;

        double Integral=0;

        while(true) {
            byte[] buffer = new byte[256];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socket.receive(request);
            if (request.getLength() != 0) {
                String Message = new String(request.getData(), 0, request.getLength());
                String strTop = "",
                        strLower = "",
                        strStep = "",
                        strInterval = "",
                        strCountOfThreads = "";

                int size = Message.length();

                int j = 0;
                while (Message.charAt(j) != ' ') {
                    strTop += Message.charAt(j);
                    j++;
                }
                j++;


                while (Message.charAt(j) != ' ') {
                    strLower += Message.charAt(j);
                    j++;
                }
                j++;


                while (Message.charAt(j) != ' ') {
                    strStep += Message.charAt(j);
                    j++;
                }
                j++;


                while (Message.charAt(j) != ' ') {
                    strInterval += Message.charAt(j);
                    j++;
                }
                j++;
                while (j != size) {
                    strCountOfThreads += Message.charAt(j);
                    j++;
                }
                int CountOfThreads = Integer.valueOf(strCountOfThreads);
                JThread[] MyThread = new JThread[CountOfThreads];


                for (int i = 0; i < CountOfThreads; i++) {
                    MyThread[i] = new JThread();
                    MyThread[i].setData((Double.valueOf(strLower) + (Double.valueOf(strInterval) * (i + 1))), (Double.valueOf(strLower) + (Double.valueOf(strInterval) * i)), Double.valueOf(strStep));
                    MyThread[i].start();

                    try
                    {
                        MyThread[i].join();
                    }
                    catch (InterruptedException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                    System.out.println(1432);
                    Integral += MyThread[i].getResult();
                }
            }

            messageSend = String.valueOf(Integral);
            Integral=0;
            byte[] buf = messageSend.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 26);
            try {
                socketSend.send(packet);
            } catch (IOException ex) {
                Logger.getLogger(JThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}