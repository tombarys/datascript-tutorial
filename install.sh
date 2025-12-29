#!/bin/bash
# Quick installer for gitbook-to-dash.bb script

set -e

INSTALL_DIR="${HOME}/.local/bin"
SCRIPT_URL="https://raw.githubusercontent.com/YOUR_USERNAME/YOUR_REPO/main/gitbook-to-dash.bb"

echo "Installing gitbook-to-dash.bb to ${INSTALL_DIR}"

# Create directory if it doesn't exist
mkdir -p "${INSTALL_DIR}"

# Download script (or copy if running locally)
if [ -f "gitbook-to-dash.bb" ]; then
    echo "Installing from local file..."
    cp gitbook-to-dash.bb "${INSTALL_DIR}/gitbook-to-dash"
else
    echo "Downloading from GitHub..."
    curl -sL "${SCRIPT_URL}" -o "${INSTALL_DIR}/gitbook-to-dash"
fi

# Make executable
chmod +x "${INSTALL_DIR}/gitbook-to-dash"

echo "✓ Installed to ${INSTALL_DIR}/gitbook-to-dash"
echo ""
echo "Make sure ${INSTALL_DIR} is in your PATH:"
echo "  export PATH=\"\${HOME}/.local/bin:\${PATH}\""
echo ""
echo "Add to ~/.zshrc or ~/.bashrc to make it permanent"
echo ""
echo "Usage: gitbook-to-dash --name \"My Docs\""
