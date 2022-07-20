<img width="300" alt="image" src="https://user-images.githubusercontent.com/88332335/179968599-d7d58e33-fbfd-43de-82b9-477c656511df.png">

# QOI - The “Quite OK Image Format” for fast, lossless image compression.
QOI — the Quite OK Image Format. It losslessly compresses RGB and RGBA images to a similar size of PNG, while offering a 20x-50x speedup in compression and 3x-4x speedup in decompression.

# Technical Details:
QOI encodes and decodes images in a single pass. It touches every pixel just once.
<br/>
Pixels are encoded as:
- a run of the previous pixel.
- an index into an array of previously seen pixels.
- a difference to the previous pixel value in r,g,b.
- full r,g,b or r,g,b,a values.
<br/>
The resulting values are packed into chunks starting with a 2- or 8-bit tag (indicating one of those methods) followed by a number of data bits. All of these chunks (tag and data bits) are byte aligned, so there's no bit twiddling needed between those chunks.

# Install & Use:
Download QOICompressor.jar and run it (you might need to install Java runtime enviorment).

# Compression
Select a file to compress (only bmp format supportet) and click 'Compress'!

# Decompression
Select an QOI compressed file (has to be provided via our compressor), and click 'Decompress'!

<!-- Note
Further description (along with an updated version of the project) coming soon! -->
