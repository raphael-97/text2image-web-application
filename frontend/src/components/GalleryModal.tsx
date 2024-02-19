"use client";
import { Modal, ModalBody, ModalContent } from "@nextui-org/react";
import Image from "next/image";
import { useRouter } from "next/navigation";
import React from "react";

export default function GalleryModal(params: { id: string }) {
  const router = useRouter();
  return (
    <Modal
      defaultOpen
      backdrop="opaque"
      hideCloseButton
      className="bg-inherit shadow-none"
      size="lg"
      placement="top-center"
      onClose={() => router.back()}
    >
      <ModalContent>
        <ModalBody>
          <div className="flex justify-center">
            <Image
              src={`/api/images/${params.id}`}
              alt={`img_${params.id}`}
              width={512}
              height={512}
              className="rounded-3xl"
            ></Image>
          </div>
        </ModalBody>
      </ModalContent>
    </Modal>
  );
}
