/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.primefinder;

import java.io.IOException;

/**
 *
 */
public class Control extends Thread {
    
    private final static int NTHREADS = 3;
    private final static int MAXVALUE = 30000000; //30000000
    private final static int TMILISECONDS = 3000;

    private final int NDATA = MAXVALUE / NTHREADS;

    public static final PauseControl pauseControl = new PauseControl(); //

    private PrimeFinderThread pft[];
    
    private Control() {
        super();
        this.pft = new  PrimeFinderThread[NTHREADS];

        int i;
        for(i = 0;i < NTHREADS - 1; i++) {
            PrimeFinderThread elem = new PrimeFinderThread(i*NDATA, (i+1)*NDATA);
            pft[i] = elem;
        }
        pft[i] = new PrimeFinderThread(i*NDATA, MAXVALUE + 1);
    }
    
    public static Control newControl() {
        return new Control();
    }

    @Override
    public void run() {
        for(int i = 0;i < NTHREADS;i++ ) {
            pft[i].start();
        }

        // Bucle de control mientras alguno de los hilos siga vivo
        while (algunHiloVivo()) {
            try {
                Thread.sleep(TMILISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Activa la pausa
            synchronized(pauseControl) {
                pauseControl.setPaused(true);
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            int totalPrimos = 0;
            for (int i = 0; i < NTHREADS; i++) {
                totalPrimos += pft[i].getPrimes().size();
            }

            System.out.println("Número de primos encontrados hasta el momento: " + totalPrimos);
            System.out.println("Presione ENTER para continuar...");

            // El usuario presiona ENTER
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            synchronized(pauseControl) {
                pauseControl.setPaused(false);
                pauseControl.notifyAll();
            }
        }
    }

    private boolean algunHiloVivo() {
        for (int i = 0; i < NTHREADS; i++) {
            if (pft[i].isAlive()) {
                return true;
            }
        }
        return false;
    }
    
}
