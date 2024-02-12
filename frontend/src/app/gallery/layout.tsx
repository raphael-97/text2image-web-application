import React from "react";

export default function PhotosLayout({
  children,
  modal,
}: {
  modal: React.ReactNode;
  children: React.ReactNode;
}) {
  return (
    <>
      {children}
      {modal}
    </>
  );
}
