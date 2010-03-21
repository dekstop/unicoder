
2010-03-14 18:46:07

unicoder -- a human-readable unicode caesar cypher

Unicoder is a low-tech attempt at generating text that is readable but not 
findable. We do this by replacing characters using the Unicode character set.

Úñīçℴḑℯʳ ḯș ă ⅼŏŵ-ťẻčḥ ảṫţėⅿᵖᵗ ặṫ ģệņểṝȁṭĩňḡ ṯềẍᵗ ţẖąţ ǐś řėₐⅾȃḅļȩ ḃưṱ ṇõţ 
ḟíņďǡḇļȅ․ Ẅế ḑṓ ťḧȋṩ ḇỳ ŕểṕḽąⅽǐńℊ ĉḩǎṙåčṫęʳṧ ǖŝïńġ ṭℎệ Ṻṋḭçôᵈễ ćḧḁṝǡčťéᵣ ˢȩṫ‧

As you can see this is an early version... the hardest part is picking good
replacement characters.

There are two parts to this project:
* Tools to build character replacement maps.
* A script to encode plain text based on a character replacement map.

To set up a shortcut in TextMate:
* Open the bundle editor, create a new command
* Command: echo "$TM_SELECTED_TEXT" | unicoder <unicoder map file>
* Select a keyboard shortcut, I use ctrl+alt+cmd+u

The first replacement map was generated based on Unicode's notion of 
compatibility character equivalence (or more specifically, by building a map
of the normalization form compatibility decomposition of all code points.) 
This was followed by a bit of manual trimming of the character set.

http://en.wikipedia.org/wiki/Unicode_equivalence

TODO: use octropus to build character maps based on visual similarity
