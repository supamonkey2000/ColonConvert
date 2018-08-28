package com.rajosh.colonconvert;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

/**

    ###########                     #######                                        ###########
  ##############                    #######                                      ###############                                                                                 #######
 #######  #######      #######      #######      #######       ######  #####    #######  #######      #######       ######  #####   ######   ######    #######       ######  ##  #######
 #######  #######    ############   #######    ############    ###############  #######  #######    ############    ##############  ######   #####   ############    ########## ##########
 #######  #######   ######  ######  #######   ######  ######   ####### #######  #######  #######   ######  ######   ######  ####### ######  ######  ######  ######   ##########  #######
 #######  #######  #######  ######  #######   ######  #######  ######  #######  #######  #######  #######  #######  ######  #######  #####  ###### #######  ######   ##########  #######
 #######           #######  ######  #######  #######  #######  ######  #######  #######           #######  #######  ######  #######  ###### ###### ################  #######     #######
 #######           #######  ######  #######  #######  #######  ######  #######  #######           #######  #######  ######  #######  ###### #####  ################  ######      #######
 #######  #######  #######  ######  #######  #######  #######  ######  #######  #######  #######  #######  #######  ######  #######  ###### #####  #######           ######      #######
 #######  #######  #######  ######  #######  #######  #######  ######  #######  #######  #######  #######  #######  ######  #######   ##### #####  #######  #######  ######      #######
 #######  #######  #######  ######  #######   ######  #######  ######  #######  #######  #######  #######  ######   ######  #######   ##########   #######  ######   ######      #######
  ###############   ###### #######  #######   ####### ######   ######  #######   ###############   ##############   ######  #######   ##########    ####### ######   ######       ########
   ############      ############   #######    ############    ######  #######    ############      ############    ######  #######    #########     ############    ######       ########

 ColonConvert
 Converts an image to ASCII character text because why not.
 Author: Joshua Moore
 Website: https://github.com/supamonkey2000/ColonConvert
 License:
 MIT License (plus modification)

 Copyright (c) 2018 Joshua Moore

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software. If you do not honor the
 license I will personally hunt you down (jk).

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.

 */
public class ColonConvert {
    private static BufferedImage image; // Raw image is stored in this
    private static File outFile; // Output file the text is saved to
    private static String character = ":"; // Pixels are replaced by this
    private static int scale = 2, width, height;

    /**
     * Called from command line. Parses arguments and saves file/prints output.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) { // The user did NOT supply a file
            System.err.println("No image supplied! Please try again.");
            System.err.println("Correct usage: > java -jar ColonConvert.jar image.png");
            exit(1);
        }
        if (!args[0].toLowerCase().endsWith(".png")) { // The user did NOT supply a PNG file
            System.err.println("Image must be a PNG! Please try again.");
            exit(1);
        }

        // Iterate through the arguments and parse them
        for (String arg : args) {
            if (arg.toLowerCase().endsWith(".png")) { // Load the image
                try {
                    image = ImageIO.read(new File(arg));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    exit(1);
                }
            } else if (arg.toLowerCase().startsWith("-o")) { // Create the output file
                outFile = new File(arg.split("=")[1]);
                try {
                    if (!outFile.createNewFile() && !outFile.exists()) {
                        throw new IOException("Failed to create output file!");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    exit(1);
                }
            } else if (arg.toLowerCase().startsWith("-c")) { // Change the character
                character = arg.split("=")[1];
            } else if (arg.toLowerCase().startsWith("-s")) { // Change the scale
                scale = Integer.parseInt(arg.split("=")[1]);
            }
        }

        // Create a new size to match the scale
        width = image.getWidth() / scale;
        height = image.getHeight() / scale;
        height /= 2; // This fixes the output so it doesn't look super tall

        // Contains the converted and formatted string
        String converted = convertImage();

        if (outFile == null) { // They didn't want to save it so we print it
            System.out.println(converted);
        } else { // They want to save it so we save it
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
                writer.write(converted);
                writer.flush();
                System.out.println("Saved file.");
            } catch (IOException ex) {
                ex.printStackTrace();
                exit(1);
            }
        }

        exit(0);
    }

    /**
     * Converts the image into a formatted string
     * @return The formatted string of the image
     */
    private static String convertImage() {

        // Scale the image
        Color[][] scaled = new Color[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                scaled[x][y] = new Color(image.getRGB(x * scale, (y * scale) * 2), true);
            }
        }


        // Parse the transparency
        String[][] parsed = new String[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isTransparent(scaled[x][y])) {
                    parsed[x][y] = " ";
                }
                else parsed[x][y] = character;
            }
        }

        // Merge into single string
        String returnString = "";
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                returnString = returnString.concat(parsed[x][y]);
            }
            returnString = returnString.concat("\n");
        }
        return returnString;
    }

    /**
     * Determine if the pixel is transparent
     * @param color The pixel data
     * @return Whether or not the pixel is transparent
     */
    private static boolean isTransparent(Color color) {
        int pixel = color.getRGB();
        return (pixel >> 24) == 0x00; // Bit-shift witchcraft
    }

    /**
     * Exit the program
     * @param statusCode Exit code to report to the system
     */
    private static void exit(int statusCode) {
        System.exit(statusCode);
    }
}

/*

                                                                                 :
                                                                                 ::
                                                                                 :::
                                                                                 :::
                                                                                 :::
                                                                                 :::
                                                                                ::::
                                                                               :::::
                                                                              :::::
                                                                             :::::
                                                                           ::::::
                                                                         :::::::
                                                                       ::::::::
                                                                     ::::::::
                                                                   :::::::::                    ::
                                                                ::::::::::                :::::
                                                              ::::::::::             ::::::
                                                           ::::::::::            :::::::
                                                         ::::::::::           :::::::
                                                       ::::::::::          ::::::::
                                                     :::::::::           :::::::
                                                   ::::::::::          :::::::
                                                  :::::::::          ::::::::
                                                 :::::::::          ::::::::
                                                :::::::::          ::::::::
                                                ::::::::           ::::::::
                                                ::::::::           ::::::::
                                                ::::::::           :::::::::
                                                 :::::::           ::::::::::
                                                  ::::::            :::::::::::
                                                   ::::::            :::::::::::
                                                     :::::            :::::::::::
                                                      :::::            :::::::::::
                                                        :::::            ::::::::::
                                                          ::::            :::::::::
                                                            :::            :::::::
                                                              :::          ::::::
                                                                ::         :::::
                                                                           :::                       ::::::::::::
                                     ::::::::                             ::                                :::::::
                               ::::::::                                 :                                      ::::::
                           :::::::::                                                       :::::                ::::::
                          ::::::::::::::                                   :::::::::::::::::                    ::::::
                            ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::                          ::::::
                                      ::::::::::::::::::::::::::::::::::                                        ::::::
                                                                                                               ::::::
                                                                                                             :::::::
                                        :::                                                                 ::::::
                                     ::::::                                        :::                   ::::::
                                     ::::::::::::::::::            ::::::::::::::::::::::              :::::
                                       ::::::::::::::::::::::::::::::::::::::::::::::::::          :::::
                                            :::::::::::::::::::::::::::::::::::::               :::


                                              ::
                                          :::::
                                         :::::::::::::::              ::::::::::::::
                                          ::::::::::::::::::::::::::::::::::::::::::::::
                                             ::::::::::::::::::::::::::::::::::::::::
                             :::::::               ::::::::::::::::::::::::::
                      ::::::::                                                                                :
                  :::::::                                                                                     ::
                ::::::::                                                                                   ::::
               ::::::::::::::                                                                      :::::::::
                :::::::::::::::::::::::::                                         ::::::::::::::::::::              ::
                       :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::                  :::::
                                       :::::::::::::::::::::::::::::::::::::::::                          ::::::::
                                                                                                  ::::::::::::
                                  :::::::                                           ::::::::::::::::::::
                                          ::::::::::::::::::::::::::::::::::::::::::::::::::::





                  :::::::
                  :::::::
                  :::::::
                  :::::::
                  :::::::
                  :::::::            ::::::::::::::         ::::::::             :::::::         :::::::::::::         :::: ::  ::
                  :::::::       ::::::::::::::::::::::       :::::::             :::::::    :::::::::::::::::::::       ::  ::::::
                  :::::::        ::::::::::::::::::::::       :::::::            :::::::    :::::::::::::::::::::::     ::  : :: :
                  :::::::                       ::::::::      ::::::::          :::::::                     :::::::
                  :::::::                        :::::::       :::::::          :::::::                      :::::::
                  :::::::                        :::::::       ::::::::        :::::::                       :::::::
                  :::::::                        :::::::        ::::::::       :::::::                       :::::::
                  :::::::             ::::::::::::::::::         :::::::      :::::::            :::::::::::::::::::
                  :::::::         ::::::::::::::::::::::         ::::::::     :::::::         ::::::::::::::::::::::
                  :::::::       :::::::::        :::::::          :::::::    :::::::        :::::::::        :::::::
                  :::::::      :::::::           :::::::          ::::::::   ::::::        :::::::           :::::::
                  :::::::      :::::::           :::::::           :::::::  :::::::       :::::::            :::::::
                  :::::::      :::::::           :::::::            ::::::::::::::        :::::::            :::::::
                  :::::::      ::::::::        :::::::::            :::::::::::::         ::::::::        ::::::::::
                  :::::::       ::::::::::::::::::::::::             :::::::::::           :::::::::::::::::::::::::
                  :::::::        ::::::::::::::::  :::::              :::::::::             ::::::::::::::::  ::::::
                 ::::::::           :::::::::      :::::               :::::::                 :::::::::       ::::
                 :::::::
                 ::::::
                :::::::
              :::::::
                :::

 */
