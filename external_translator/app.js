const express = require('express')
const app = express()
const port = 3000

const morseCodes = {
  ".-":   "a",
  "-...": "b",
  "-.-.": "c",
  "-..":  "d",
  ".":    "e",
  "..-.": "f",
  "--.":  "g",
  "....": "h",
  "..":   "i",
  ".---": "j",
  "-.-":  "k",
  ".-..": "l",
  "--":   "m",
  "-.":   "n",
  "---":  "o",
  ".--.": "p",
  "--.-": "q",
  ".-.":  "r",
  "...":  "s",
  "-":    "t",
  "..-":  "u",
  "...-": "v",
  ".--":  "w",
  "-..-": "x",
  "-.--": "y",
  "--..": "z",
  ".----": "1",
  "..---": "2",
  "...--": "3",
  "....-": "4",
  ".....": "5",
  "-....": "6",
  "--...": "7",
  "---..": "8",
  "----.": "9",
  "-----": "0"
}

app.get('/morse', (req, res) => {
  console.log(req.query.code)
  var code = morseCodes[req.query.code];
  if (code) {
    res.send(code);
  } else {
    res.sendStatus(404);
  }
})

app.listen(port, () => console.log(`Example app listening at http://localhost:${port}`))

