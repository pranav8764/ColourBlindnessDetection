# Assets Folder

This folder contains the assets used by the application, especially the Ishihara test plate images.

## Image Assets

The Ishihara test plate images are located in the `images` directory. When building the app, these images will be automatically copied to the Android assets directory.

## Important Note

To ensure the images are properly included in the app:

1. Make sure the images are in the correct format (PNG)
2. Ensure they are properly named (Screenshot*.png)
3. The app will look for these images in the `images` directory of the Android assets

## Directory Structure

```
- assests/
  - images/
    - Screenshot 2025-04-21 210205.png (Number 12)
    - Screenshot 2025-04-21 210222.png (Number 8)
    - Screenshot 2025-04-21 210248.png (Number 29)
    - Screenshot 2025-04-21 210301.png (Number 5)
    - Screenshot 2025-04-21 210321.png (Number 3)
    - Screenshot 2025-04-21 210341.png (Number 15)
    - Screenshot 2025-04-21 210355.png (Number 74)
    - Screenshot 2025-04-21 210417.png (Number 6)
    - Screenshot 2025-04-21 210429.png (Number 45)
    - Screenshot 2025-04-21 210443.png (Number 16)
```

These images will be copied to the Android assets directory during build.
