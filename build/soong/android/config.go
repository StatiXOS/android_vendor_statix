package android

// Global config used by Lineage soong additions
var StatixConfig = struct {
	// List of packages that are permitted
	// for java source overlays.
	JavaSourceOverlayModuleWhitelist []string
}{
	// JavaSourceOverlayModuleWhitelist
	[]string{
		"org.statix.hardware",
	},
}
