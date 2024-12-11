# Huffman Coding Compression/Decompression

This project implements a lossless file compression and decompression algorithm using **Huffman Coding**. Huffman Coding assigns variable-length prefix codes to characters based on their frequency in the input file, ensuring efficient compression while maintaining the ability to decode the data without ambiguity. 

## Project Overview
The project performs the following steps:

1. **Reading the input file**: The program reads the specified file and counts the frequency of each byte in the file.
2. **Building the Huffman tree**: The program constructs a Huffman tree based on the byte frequencies.
3. **Generating Huffman codes**: A table of Huffman codes is generated and displayed, mapping each byte to its corresponding binary code.
4. **Compression**: The input file is encoded using the generated Huffman codes, and the compressed data is written to a new file.
5. **Decompression**: The compressed file is read, decoded using the Huffman tree, and the original content is output to a third file.

## Current Status
The code is not fully functional at the moment. While the core logic for encoding and decoding is implemented, there are still some bugs preventing the program from operating as expected. The goal is to complete the implementation and fix these issues.

## How to Use
1. Compile and run the program.
2. Provide an input file for compression.
3. The program will generate a compressed file and display the encoding table.
4. Use the decompressed output to check if the uncompressed file matches the original.
