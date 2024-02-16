"use client";
import RegisterForm from "@/components/RegisterForm";
import {
  Card,
  CardBody,
  Modal,
  ModalBody,
  ModalContent,
  ModalHeader,
} from "@nextui-org/react";
import { useRouter } from "next/navigation";

export default function RegisterModal() {
  const router = useRouter();

  return (
    <Modal
      defaultOpen
      backdrop="opaque"
      hideCloseButton
      className="bg-inherit shadow-none"
      size="lg"
      placement="center"
      onClose={() => router.back()}
    >
      <ModalContent className="flex items-center justify-center">
        <ModalHeader className="flex flex-col gap-1 items-center justify-center">
          Sign Up Below
        </ModalHeader>
        <ModalBody>
          <Card className="max-w-full w-[340px] h-[600px]">
            <CardBody>
              <RegisterForm />
            </CardBody>
          </Card>
        </ModalBody>
      </ModalContent>
    </Modal>
  );
}
