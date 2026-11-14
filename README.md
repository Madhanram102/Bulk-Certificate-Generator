# Bulk Certificate Generator

A lightweight **Java** tool that generates professional certificates in bulk. Give it a CSV file of participants and it produces one print-ready PNG certificate for each person in seconds.

![Java](https://img.shields.io/badge/Language-Java-orange?logo=openjdk&logoColor=white)
![Dependencies](https://img.shields.io/badge/Dependencies-None-brightgreen)
![Status](https://img.shields.io/badge/Status-Complete-blue)

---

## Features

- Generates certificates in bulk from a single CSV file
- A4 landscape output at 150 DPI, ready to print
- Long names and course titles are auto-resized to fit
- Handles quoted CSV values (`"Smith, John"`) and Excel BOM files
- Skips incomplete rows and reports failures without crashing
- Uses only the Java standard library, so there is nothing to install

---

## Project Structure

```
Bulk-Certificate-Generator/
├── src/
│   ├── Main.java                  # Entry point and CLI
│   ├── Participant.java           # Data model (name, course, date)
│   ├── CsvReader.java             # CSV parser
│   └── CertificateGenerator.java  # Draws and saves each certificate
├── data/
│   └── participants.csv           # Sample input
├── .gitignore
└── README.md
```

---

## Getting Started

### Prerequisites

- JDK 8 or higher (`javac -version` to check)

### 1. Clone the repository

```bash
git clone https://github.com/Madhanram102/Bulk-Certificate-Generator.git
cd Bulk-Certificate-Generator
```

### 2. Compile

```bash
mkdir out
javac -d out src/*.java
```

On Windows PowerShell, use:

```powershell
mkdir out
javac -d out (Get-ChildItem src\*.java)
```

### 3. Run

```bash
java -cp out Main
```

Certificates are saved in the `output/` folder.

---

## Usage

```bash
java -cp out Main [csvFile] [outputFolder] ["Organization Name"]
```

| Argument | Default | Description |
|----------|---------|-------------|
| `csvFile` | `data/participants.csv` | Input file with participants |
| `outputFolder` | `output` | Where certificates are saved |
| `Organization Name` | `HCL Hackathon` | Text shown at the top of the certificate |

Example:

```bash
java -cp out Main data/participants.csv certificates "Tech Fest 2026"
```

---

## CSV Format

The first row must be a header. `name` and `course` are required; `date` is optional (today's date is used when empty).

```csv
name,course,date
Madhanram M,Java OOP Fundamentals,14 November 2026
Priya Sharma,Data Structures and Algorithms,14 November 2026
"Fatima Begum S",Full Stack Web Development,
```

---

## Sample Output

Console:

```
=== Bulk Certificate Generator ===
Input  : data/participants.csv
Output : output

  [ok]   001_Madhanram_M.png
  [ok]   002_Priya_Sharma.png
  ...

Done. Generated: 7  |  Failed: 0
```

Each certificate includes the organization name, the participant's name, the course, the date, a gold seal, and signature lines.

---

## How It Works

1. `CsvReader` parses the CSV into a list of `Participant` objects.
2. `CertificateGenerator` draws each certificate with Java 2D (`Graphics2D`) onto a `BufferedImage`.
3. `Main` saves each image as `NNN_Name.png` and prints a summary.

---

## Customization

All styling lives in `CertificateGenerator.java`:

- **Colors**: edit the `PAPER`, `NAVY`, `GOLD`, `INK` constants
- **Size**: change `W` and `H`
- **Text and layout**: edit `drawText()` and `drawSignatures()`

---

## Possible Improvements

- Export as PDF
- Add a logo or signature image
- Email certificates automatically
- Simple GUI to pick the CSV file

---

## Author

**Madhanram M**
GitHub: [@Madhanram102](https://github.com/Madhanram102)
