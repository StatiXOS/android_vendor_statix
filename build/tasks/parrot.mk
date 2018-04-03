.PHONY: meme
meme: $(shell timeout 5s curl parrot.live)
