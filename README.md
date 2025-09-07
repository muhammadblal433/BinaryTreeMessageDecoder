# Binary Tree Message Decoder

This project implements a **binary tree-based message decoder** for messages compressed using a custom binary encoding scheme. It builds a decoding tree from a pre-order encoded string and decodes a binary message stored in a `.arch` file. The project also outputs character encoding, decoded message, and statistics such as average bits per character and space savings.

## How It Works

1. **Tree Construction:**  
   The `.arch` file begins with a **pre-order encoded string** of the binary tree. Internal nodes are denoted by `^`, while leaf nodes represent characters. The tree is built **iteratively** using a stack to manage parent-child assignments.

2. **Message Decoding:**  
   After tree construction, the program reads the binary string at the bottom of the `.arch` file and uses the tree to decode it.  
   `'0'` means go left, `'1'` means go right.

3. **Output Includes:**
   - Character to binary code mapping
   - Decoded message
   - Statistics:
     - Average bits per character
     - Total characters decoded
     - Space savings percentage

---

## How to Run

### Requirements
- Java 8+
- `.arch` file with pre-order tree and encoded binary message

### Compile

```bash
javac MsgTree.java
