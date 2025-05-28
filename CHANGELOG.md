# Hexcassettes

## 2.0.0
- added screen for managing cassettes
  - allows for mishapping cassettes to still be cancelled even when your staff is wrenched from your hand
- added reflection for whether something is running in a cassette and its index
- added variant of Enqueue that requires no index
- changed cassettes to use a pattern rather than a label
- changed cassettes to dequeue before they cast so Program Purification can not self-read
- changed to storing cassette data in player rather than persistent state
- removed cassettes from the staff casting screen
- rewrote a lot of code under the hood

## 1.1.4
- minor bug fix: cassettes don't clear on crossing End Portal

## 1.1.3
- minor bug fix: cassettes are now in the staff screen after wrong deletion

## 1.1.2
- updated to 1.20.1
- removed cassettes from chat screen

## 1.1.1
- fixed a bug with large lists that caused crash loops

## 1.1.0
- added advancement for eating a cassette and having a full album
- added cassettes to the staff casting screen
- added warning about rapidly renaming cassettes
- changed position of cassettes to lower-right corner
- changed hand used for casting to offhand if mainhand contains item
- fixed location description of cassettes
- fixed parameters for Program Purifications
- fixed bug where using same harness as player results in introspection and consideration cancelling the cassette

## 1.0.0
- initial release
