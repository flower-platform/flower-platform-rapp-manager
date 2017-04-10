package org.flowerplatform.rapp_manager.arduino_ide.library_manager.compatibility;

/**
 * For v < v1.6.6.
 * 
 * @author Cristian Spiescu
 */
public class LibraryInstallerWrapperPre166{

//	protected Method remove;
//	protected Method install;
//	
//	public static LibrariesIndexer librariesIndexer;
//
//	public LibraryInstallerWrapperPre166() {
//		super();
//		try {
//			remove = installer.getClass().getDeclaredMethod("remove", ContributedLibrary.class);
//			install = installer.getClass().getDeclaredMethod("install", ContributedLibrary.class, ContributedLibrary.class);
//		} catch (NoSuchMethodException | SecurityException e) {
//			FlowerPlatformPlugin.log("Cannot delegate to LibraryInstaller", e);
//		}
//	}
//
//	@Override
//	protected void initLibraryInstaller() {
//		try {
//			Field liField = BaseNoGui.class.getDeclaredField("librariesIndexer");
//			liField.setAccessible(true);
//			librariesIndexer = (LibrariesIndexer) liField.get(null);
//			
//			installer = new LibraryInstaller(BaseNoGui.getPlatform());
//		} catch (NoSuchFieldException | SecurityException
//				| IllegalArgumentException | IllegalAccessException e) {
//			FlowerPlatformPlugin.log("Cannot instantiate LibraryInstaller", e);
//		}
//	}
//
//	@Override
//	public void remove(Library lib) throws Exception {
//		remove.invoke(installer, lib.getUserLibrary().);
//	}
//
//	@Override
//	public void install(Library lib, Library replacedLib) throws Exception {
//		install.invoke(installer, lib.getUserLibrary(), replacedLib.getUserLibrary());
//		FlowerPlatformPlugin.log("Please restart Arduino IDE, so that the installed lib is taken into account.");
//	}

}
