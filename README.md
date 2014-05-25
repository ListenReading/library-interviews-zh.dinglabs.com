Adapting content from [Cultural Interviews with Chinese-Speaking Professionals](http://www.laits.utexas.edu/orkelm/chinese/), for educational purposes.

## Process

1. Downloaded HTML files from [http://www.laits.utexas.edu/orkelm/chinese/](http://www.laits.utexas.edu/orkelm/chinese/)
2. Extracted the content from those files, into output.csv.
3. Merged that data into a Google Spreadsheet, which has corresponding YouTube video IDs and section names.
   https://docs.google.com/spreadsheets/d/1Bft1o-0IoJsn8e2zMcL0vs_bRebfRMNw_TuxiSjwwWA/edit#gid=0
   Saved it as CSV under input/online_spreadsheet.csv
4. Use that CSV as input to generate HTML files.

## Future Enhancements
- [ ] Add 404 page
- [ ] Add link to source on every page: "This is reformatted content from "this website", for educational purposes. Fork the source code on GitHub."  

- [ ] Generate new displays for the content
  - build-in popup dictionary
  - English and Chinese side-by-side
  - shortcut-key, like spacebar, to play/pause audio
  - auto-play mode, auto-advancing to next video
- [ ] create parallel texts, sentence-aligned or even finer-grained
- [ ] add time-codes for the sentence-aligned content
- [ ] generate subtitle tracks from the time-aligned content, which can be added to the existing videos, or displayed together in an HTML player.