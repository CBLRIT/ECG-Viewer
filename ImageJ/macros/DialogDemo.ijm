// This macro demonstrates how a macro can display a
// data input dialog box. The dialog it creates contains
// one string field, one popup menu, two numeric fields,   
// and one check box.

  title = "Untitled";
  width=512; height=512;
  Dialog.create("New Image");
  Dialog.addString("Title:", title);
  Dialog.addChoice("Type:", newArray("8-bit", "16-bit", "32-bit", "RGB"));
  Dialog.addNumber("Width:", 512);
  Dialog.addNumber("Height:", 512);
  Dialog.addCheckbox("Ramp", true);
  Dialog.show();
  title = Dialog.getString();
  width = Dialog.getNumber();
  height = Dialog.getNumber();;
  type = Dialog.getChoice();
  ramp = Dialog.getCheckbox();
  if (ramp==true) type = type + " ramp";
  newImage(title, type, width, height, 1);

