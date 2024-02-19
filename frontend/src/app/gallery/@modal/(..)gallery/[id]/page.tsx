import { CheckIfUserHasAccessToImage } from "@/app/lib/imageAuthActions";
import GalleryModal from "@/components/GalleryModal";
import { notFound } from "next/navigation";
import React from "react";

export default async function InterceptingModal({
  params,
}: {
  params: { id: string };
}) {
  try {
    await CheckIfUserHasAccessToImage(parseInt(params.id));
  } catch (error) {
    if (error instanceof Error) notFound();
  }
  return <GalleryModal id={params.id} />;
}
